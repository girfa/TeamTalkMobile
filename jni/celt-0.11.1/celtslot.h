#include "libcelt/celt.h"

typedef struct SlotEncoder {
	CELTMode *mode;
	CELTEncoder *state;
	int channels;
} SlotEncoder;

typedef struct SlotDecoder {
	CELTMode *mode;
	CELTDecoder *state;
	int channels;
} SlotDecoder;

struct SEVector {
	struct SlotEncoder **ses;
	int size;
};

struct SDVector {
	struct SlotDecoder **sds;
	int size;
};

int initSE(struct SEVector *sev);
int initSD(struct SDVector *sdv);
