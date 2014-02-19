LOCAL_PATH			:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE		:= theora-1.1.1
LOCAL_C_INCLUDES	:= $(LOCAL_PATH)/include
LOCAL_CFLAGS		:= -Werror
LOCAL_LDLIBS		:= -llog
LOCAL_SRC_FILES		:= \
lib/analyze.c \
lib/apiwrapper.c \
lib/bitpack.c \
lib/cpu.c \
lib/decapiwrapper.c \
lib/decinfo.c \
lib/decode.c \
lib/dequant.c \
lib/encapiwrapper.c \
lib/encfrag.c \
lib/encinfo.c \
lib/encode.c \
lib/enquant.c \
lib/fdct.c \
lib/fragment.c \
lib/huffdec.c \
lib/huffenc.c \
lib/idct.c \
lib/info.c \
lib/internal.c \
lib/mathops.c \
lib/mcenc.c \
lib/quant.c \
lib/rate.c \
lib/state.c \
lib/tokenize.c \
LibTheora.c

include $(BUILD_SHARED_LIBRARY)