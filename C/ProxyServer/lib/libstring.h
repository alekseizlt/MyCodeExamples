#ifndef libstring
#define libstring
//==============================================================
// Sting to lower case
void stringToLowerCase(char *paramString);

//==============================================================
// Sting to upper case
void stringToUpperCase(char *paramString);

//==============================================================
// String without last Enter
void stringWithoutLastEnter(char *paramString);

//==============================================================
// Function for compare two substrings
int stringCompare(char *paramString1, char *paramString2);

//==============================================================
// Function for count substrings in string
int stringCountSubStrings(char *paramString, const char *paramDelimiter);

//==============================================================
// Function for split string to array
char **stringSplit(char *paramString, const char *paramDelimiter, int *paramCountSubStrings);

//==============================================================
// Function for get substring
char *getSubString(char *paramString, int paramStart, int paramLength);

//==============================================================
// Function get index of string search
int getIndexOf(char *paramString, const char *paramSearch);

//==============================================================
// Function get last index
int getLastIndexOf(char *paramString, const char *paramSearch);

//==============================================================
// Replace substrings in string
char *stringReplace(char *paramString, const char *paramSearch, const char *paramReplace);
#endif