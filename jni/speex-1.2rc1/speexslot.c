#include <stdlib.h>
#include <string.h>

#include "speexslot.h"

int initV(struct SVector *sv) {
	if (sv->ss == 0) {
			sv->size = 1;
			sv->ss = malloc(sizeof(struct Slot*));
			sv->ss[0] = (void*) 0;
		}
		int s;
		for (s = 0; s < sv->size; s++) {
			if ((void*)0 == sv->ss[s]) break;
		}
		if (s >= sv->size) {
			struct Slot** new = malloc((1 + sv->size) * sizeof(Slot*));
			memcpy(new, sv->ss, sv->size * sizeof(Slot*));
			new[sv->size] = (void*)0;
			free(sv->ss);
			sv->ss = new;
			sv->size++;
		}
	    return s;
}
