#include <jni.h>
#include <android/log.h>

#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, "libspeex-1.2rc1.so", __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, "libspeex-1.2rc1.so", __VA_ARGS__))

#include <stdlib.h>
#include <speexslot.h>

static struct SVector sv = {0, 0};
int error = 0;

JNIEXPORT jint JNICALL
Java_com_girfa_apps_teamtalk4mobile_api_jni_LibSpeex_initD(
		JNIEnv* env, jobject obj,
		jint bandMode, jint channels, jint quality,
		jboolean vbr, jint bitRate, jint maxBitRate, jboolean dtx) {
	int id = initV(&sv);
	sv.ss[id] = malloc(sizeof(struct Slot));
	struct Slot* cs = sv.ss[id];

	const SpeexMode* mode;
	switch (bandMode) {
		case 1:
			mode = &speex_wb_mode;
			break;
		case 2:
			mode = &speex_uwb_mode;
			break;
		default:
			mode = &speex_nb_mode;
			break;
	}
	cs->state = speex_decoder_init(mode);
	cs->channels = channels;
	if (channels == 2) cs->stereo = speex_stereo_state_init();

	if (vbr) {
		error = speex_decoder_ctl(cs->state, SPEEX_SET_VBR_QUALITY, &quality);
		error = speex_decoder_ctl(cs->state, SPEEX_SET_BITRATE, &bitRate);
		error = speex_decoder_ctl(cs->state, SPEEX_SET_VBR_MAX_BITRATE, &maxBitRate);
		error = speex_decoder_ctl(cs->state, SPEEX_SET_DTX, &dtx);
	} else {
		error = speex_decoder_ctl(cs->state, SPEEX_SET_QUALITY, &quality);
	}
	if (error != 0) {
		if (error == -1) LOGE("initD.unknown request");
		else if (error == -2) LOGE("initD.invalid parameter");
		return -1;
	}
	speex_bits_init(&cs->bits);
	return id;
}

JNIEXPORT void JNICALL
Java_com_girfa_apps_teamtalk4mobile_api_jni_LibSpeex_destroyD(
		JNIEnv* env, jobject obj, jint id) {
	if (id < 0) return;
	struct Slot* cs = sv.ss[id];
	if (cs == NULL) return;

	speex_bits_destroy(&cs->bits);
	speex_decoder_destroy(cs->state);
	free(cs);
	cs = (void*)0;
}

JNIEXPORT jshortArray JNICALL
Java_com_girfa_apps_teamtalk4mobile_api_jni_LibSpeex_decode(
		JNIEnv* env, jobject obj, jint id, jbyteArray frame) {
	if (id < 0 || frame == NULL) return NULL;
	struct Slot* cs = sv.ss[id];
	if (cs == NULL) return NULL;

	int length = (*env)->GetArrayLength(env, frame);
	char *input = (*env)->GetByteArrayElements(env, frame, 0);
	speex_bits_read_from(&cs->bits, input, length);

	int frameSize;
	speex_decoder_ctl(cs->state, SPEEX_GET_FRAME_SIZE, &frameSize);
	jshortArray pcm = (*env)->NewShortArray(env, frameSize);
	short *output = (*env)->GetShortArrayElements(env, pcm, 0);
	error = speex_decode_int(cs->state, &cs->bits, output);
	if (error != 0) {
		if (error == -1) LOGE("decode.end of stream");
		else if (error == -2) LOGE("decode.corrupt stream");
		return NULL;
	}

	(*env)->ReleaseByteArrayElements(env, frame, input, 0);
	(*env)->ReleaseShortArrayElements(env, pcm, output, 0);
	return pcm;
}
