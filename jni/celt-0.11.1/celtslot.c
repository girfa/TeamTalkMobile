#include <stdlib.h>
#include <string.h>

#include "celtslot.h"

int initSE(struct SEVector *sev) {
	if (sev->ses == 0) {
		sev->size = 1;
		sev->ses = malloc(sizeof(struct SlotEncoder*));
		sev->ses[0] = (void*) 0;
	}
	int se;
	for (se = 0; se < sev->size; se++) {
		if ((void*)0 == sev->ses[se]) break;
	}
	if (se >= sev->size) {
		struct SlotEncoder** new = malloc((1 + sev->size) * sizeof(SlotEncoder*));
		memcpy(new, sev->ses, sev->size * sizeof(SlotEncoder*));
		new[sev->size] = (void*)0;
		free(sev->ses);
		sev->ses = new;
		sev->size++;
	}
    return se;
}

int initDE(struct SDVector *sdv) {
	if (sdv->sds == 0) {
		sdv->size = 1;
		sdv->sds = malloc(sizeof(struct SlotDecoder*));
		sdv->sds[0] = (void*) 0;
	}
	int sd;
	for (sd = 0; sd < sdv->size; sd++) {
		if ((void*)0 == sdv->sds[sd]) break;
	}
	if (sd >= sdv->size) {
		struct SlotDecoder** new = malloc((1 + sdv->size) * sizeof(SlotDecoder*));
		memcpy(new, sdv->sds, sdv->size * sizeof(SlotDecoder*));
		new[sdv->size] = (void*)0;
		free(sdv->sds);
		sdv->sds = new;
		sdv->size++;
	}
    return sd;
}
