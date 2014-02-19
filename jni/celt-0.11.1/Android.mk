LOCAL_PATH			:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE		:= celt-0.11.1
LOCAL_C_INCLUDES	:= $(LOCAL_PATH)/include
LOCAL_CFLAGS    	:= -Drestrict=__restrict -DCUSTOM_MODES -Werror
LOCAL_LDLIBS		:= -llog
LOCAL_SRC_FILES		:= \
libcelt/bands.c \
libcelt/celt.c \
libcelt/cwrs.c \
libcelt/dump_modes.c \
libcelt/entcode.c \
libcelt/entdec.c \
libcelt/entenc.c \
libcelt/header.c \
libcelt/kiss_fft.c \
libcelt/laplace.c \
libcelt/mathops.c \
libcelt/mdct.c \
libcelt/modes.c \
libcelt/pitch.c \
libcelt/plc.c \
libcelt/quant_bands.c \
libcelt/rate.c \
libcelt/vq.c \
LibCelt.c \
celtslot.c

include $(BUILD_SHARED_LIBRARY)