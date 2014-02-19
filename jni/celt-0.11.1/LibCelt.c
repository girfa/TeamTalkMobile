#include <jni.h>
#include <android/log.h>

#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, "libcelt-0.11.1.so", __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, "libcelt-0.11.1.so", __VA_ARGS__))

#include <stdlib.h>
#include <celtslot.h>

#define FRAME_SIZE 256

static struct SEVector sev = {0, 0};
static struct SDVector sdv = {0, 0};
int error = CELT_OK;

JNIEXPORT jint JNICALL
Java_com_girfa_apps_teamtalk4mobile_api_jni_LibCelt_initD(
		JNIEnv* env, jobject obj,
		jint sampleRate, jint channels, jint bitRate, jboolean vbr) {
	int id = initDE(&sdv);
	sdv.sds[id] = malloc(sizeof(struct SlotDecoder));
	struct SlotDecoder* cds = sdv.sds[id];
	cds->channels = channels;
	cds->mode = celt_mode_create(sampleRate, FRAME_SIZE, &error);
	if (cds->mode == NULL || error != CELT_OK) {
		LOGE("initD.CELTMode error");
		LOGE(celt_strerror(error));
		return -1;
	}
	cds->state = celt_decoder_create_custom(cds->mode, channels, &error);
	if (cds->state == NULL || error != CELT_OK) {
		LOGE("initD.CELTDecoder error");
		LOGE(celt_strerror(error));
		return -1;
	}
	celt_decoder_ctl(cds->state, CELT_SET_BITRATE(bitRate));
	if (vbr) {
		celt_decoder_ctl(cds->state, CELT_SET_VBR_REQUEST);
	}
	return id;
}

JNIEXPORT void JNICALL
Java_com_girfa_apps_teamtalk4mobile_api_jni_LibCelt_destroyD(
		JNIEnv* env, jobject obj, jint id) {
	if (id < 0) return;
	struct SlotDecoder* cds = sdv.sds[id];
	if (cds == NULL) return;

	celt_decoder_destroy(cds->state);
	celt_mode_destroy(cds->mode);
	free(cds);
	cds = (void*)0;
}

JNIEXPORT jshortArray JNICALL
Java_com_girfa_apps_teamtalk4mobile_api_jni_LibCelt_decode(
		JNIEnv* env, jobject obj, jint id, jbyteArray frame) {
	if (id < 0 || frame == NULL) return NULL;
	struct SlotDecoder* cds = sdv.sds[id];
	if (cds == NULL) return NULL;

	char *input = (*env)->GetByteArrayElements(env, frame, 0);
	int length = (*env)->GetArrayLength(env, frame);

	jshortArray pcm = (*env)->NewShortArray(env, FRAME_SIZE * cds->channels);
	celt_int16 * output = (celt_int16*) (*env)->GetByteArrayElements(env, pcm, 0);

	error = celt_decode(cds->state, input, length, output, FRAME_SIZE);
	if (error) {
		LOGE("decode.CELTDecoder error");
		LOGE(celt_strerror(error));
		return NULL;
	}

	(*env)->ReleaseByteArrayElements(env, frame, input, 0);
	(*env)->ReleaseShortArrayElements(env, pcm, output, 0);
	return pcm;
}
