#include "speex/speex.h"
#include "speex/speex_stereo.h"

typedef struct Slot {
	SpeexBits bits;
	void *state;
	SpeexStereoState *stereo;
	int channels;
} Slot;

struct SVector {
	struct Slot ** ss;
	int size;
};

int initV(struct SVector *sv);
