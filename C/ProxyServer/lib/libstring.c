#include <ctype.h>
#include <stdlib.h>
#include <string.h>

//==============================================================
// Sting to lower case
void stringToLowerCase(char *paramString) {
	if(paramString != NULL) {
		for(int i = 0; i < strlen(paramString); i++) {
			*(paramString + i) = tolower(*(paramString + i));
		}
	}
}

//==============================================================
// Sting to upper case
void stringToUpperCase(char *paramString) {
	if(paramString != NULL) {
		for(int i = 0; i < strlen(paramString); i++) {
			*(paramString + i) = toupper(*(paramString + i));
		}
	}
}

//==============================================================
// String without last Enter
void stringWithoutLastEnter(char *paramString) {
	int lenStrSource = 0;

	if(paramString != NULL) {
		lenStrSource = strlen(paramString);

		if(lenStrSource > 0) {
			if(paramString[lenStrSource - 1] == '\n') {
   				paramString[lenStrSource - 1] = '\0';
   			}
		}
	}
}

//==============================================================
// Function for compare two substrings
int stringCompare(char *paramString1, char *paramString2) {
	int result = -1;

	if(paramString1 == NULL || paramString2 == NULL) {
		return -1;
	}

	if(strcmp(paramString1, paramString2) == 0) {
		return 0;
	}

	return result;
}

//==============================================================
// Function for count substrings in string
int stringCountSubStrings(char *paramString, const char *paramDelimiter) {
	int countSubStrings = 0;
	char *currentSubString;

	if(paramString == NULL || paramDelimiter == NULL) {
		return 0;
	}

	currentSubString = strtok(paramString, paramDelimiter);

	while(currentSubString != NULL) {
		countSubStrings += 1;
		currentSubString = strtok(NULL, paramDelimiter);
	}

	return countSubStrings;
}

//==============================================================
// Function for split string to array
char **stringSplit(char *paramString, const char *paramDelimiter, int *paramCountSubStrings) {
	char **returnArrayResult;
	int countSubStrings = 0;
	char *currentSubString;
	char *stringLocal;
	int i = 0;

	if(paramString == NULL || paramDelimiter == NULL) {
		return NULL;
	}

	// Get count words in phrase
	stringLocal = (char *)malloc((strlen(paramString) + 1) * sizeof(char));
	memset(stringLocal, '\0', strlen(stringLocal));
	strcpy(stringLocal, paramString);

	countSubStrings = stringCountSubStrings(stringLocal, paramDelimiter);
	*paramCountSubStrings = countSubStrings;

	strcpy(stringLocal, paramString);

	// Create array with words
	returnArrayResult = (char **)malloc(countSubStrings * sizeof(char *));

	currentSubString = strtok(stringLocal, paramDelimiter);

	while(currentSubString != NULL) {
		returnArrayResult[i] = (char *)malloc((strlen(currentSubString) + 1) * sizeof(char));
		memset(returnArrayResult[i], '\0', strlen(returnArrayResult[i]));
		strcpy(*(returnArrayResult + i), currentSubString);

		i =+ 1;
		currentSubString = strtok(NULL, paramDelimiter);
	}

	return returnArrayResult;
}

//==============================================================
// Function for get substring
char *getSubString(char *paramString, int paramStart, int paramLength) {
	int lenString = 0;

	if(paramString == NULL) {
		return NULL;
	}

	lenString = strlen(paramString);

  	if (lenString == 0 || lenString < paramStart || lenString < (paramStart + paramLength)) {
    	return NULL;
	}

  	return strndup(paramString + paramStart, paramLength);
}

//==============================================================
// Function get index of string search
int getIndexOf(char *paramString, const char *paramSearch) {
	int index = -1;
	int i = 0, j = 0;

	if(paramString == NULL || paramSearch == NULL) {
		return -1;
	}

	for(i = 0; i < strlen(paramString); i++) {
		if(paramString[i] == paramSearch[0]) {
			index = i;

			for(j = 0; j < strlen(paramSearch); j++) {
				if((i + j) >= strlen(paramString)) {
					index = -1;
					break;
				}

				if(paramString[i + j] != paramSearch[j]) {
					index = -1;
					break;
				}
			}

			if(index >= 0) {
				break;
			}
		}
	}

	return index;
}

//==============================================================
// Function get last index
int getLastIndexOf(char *paramString, const char *paramSearch) {
	int lastIndex = -1;
	int lastIndexPrev = -1;
	int i = 0, j = 0;

	if(paramString == NULL || paramSearch == NULL) {
		return -1;
	}

	for(i = 0; i < strlen(paramString); i++) {
		if(paramString[i] == paramSearch[0]) {
			lastIndex = i;

			for(j = 0; j < strlen(paramSearch); j++) {
				if((i + j) >= strlen(paramString)) {
					lastIndex = lastIndexPrev;
					break;
				}

				if(paramString[i + j] != paramSearch[j]) {
					lastIndex = lastIndexPrev;
					break;
				}
			}

			if(lastIndex >= 0) {
				lastIndexPrev = lastIndex;
			}
		}
	}

	return lastIndex;
}

//==============================================================
// Replace substrings in string
char *stringReplace(char *paramString, const char *paramSearch, const char *paramReplace) {
	char *returnValue = NULL;
	char *returnValueOld = NULL;

	int lenReturnValueOld = 0;

	char *stringTemp;
	char *substring;

	int lenReplace = 0;
	int lenSearch = 0;
	int indexSearch = -1;
	
	if(paramString == NULL || paramSearch == NULL || paramReplace == NULL) {
		return NULL;
	}

	stringTemp = (char *)malloc((strlen(paramString) + 1) * sizeof(char));
	memset(stringTemp, '\0', strlen(stringTemp));
	strcpy(stringTemp, paramString);
	
	lenReplace = strlen(paramReplace);
	lenSearch = strlen(paramSearch);
	indexSearch = getIndexOf(stringTemp, paramSearch);

	while(indexSearch >= 0) {
		substring = getSubString(stringTemp, 0, indexSearch);

		returnValueOld = returnValue;

		if(returnValueOld != NULL) {
			lenReturnValueOld = strlen(returnValueOld);
		}
		else {
			lenReturnValueOld = 0;
		}

		returnValue = (char *)malloc(
			(lenReturnValueOld + strlen(substring) + lenReplace + 1) * sizeof(char)
		);

		memset(returnValue, '\0', strlen(returnValue));

		if(returnValueOld != NULL) {
			strcpy(returnValue, returnValueOld);
			strcat(returnValue, substring);
		}
		else {
			strcpy(returnValue, substring);
		}

		strcat(returnValue, paramReplace);

		stringTemp = getSubString(stringTemp, indexSearch + lenSearch, strlen(stringTemp) - (indexSearch + lenSearch));
		indexSearch = getIndexOf(stringTemp, paramSearch);
	}

	if(stringTemp != NULL) {
		if(strlen(stringTemp) > 0) {
			returnValueOld = returnValue;

			if(returnValueOld != NULL) {
				lenReturnValueOld = strlen(returnValueOld);
			}
			else {
				lenReturnValueOld = 0;
			}

			returnValue = (char *)malloc((lenReturnValueOld + strlen(stringTemp) + 1) * sizeof(char));
			memset(returnValue, '\0', strlen(returnValue));

			if(returnValueOld != NULL) {
				strcpy(returnValue, returnValueOld);
				strcat(returnValue, stringTemp);
			}
			else {
				strcpy(returnValue, stringTemp);
			}
		}
	}

	free(stringTemp);

	return returnValue;
}