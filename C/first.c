#include <stdio.h>
#define BITS_PER_ASI_BLOCK 6


int charToASI(char ch);
char ASItoChar(int ASIval);

/*********************************
* Problem 1.3
* function getASIblock
* params:
* newWord (int)
* ind     (int)
* Return the ASI value of the charInd'th char
* in the word encoded by newWord.
*********************************/
int getASIblock(int newWord, int ind);

/*********************************
* Problem 1.4
* function isWordFull
* params:
* newWord (int)
* Return 1 if last ASI block of newWord is used
* and 0 otherwise
*********************************/
int isWordFull(int newWord);

/*********************************
* Problem 1.5
* function newWordType
* params:
* newWord (int)
* Returns a value associated with the word type
*********************************/
int newWordType(int newWord);
/*********************************
* Problem 1.6
* function appendWordChar
* params:
* newWord (int)
* ch      (char)
* Return the new word obtained by adding the ASI 
* value of char ch to the end of newWord.
*********************************/
int appendWordChar(int newWord, char ch);

/*********************************
* Problem 1.7
* function printNewWord
* params:
* newWord (int)
* Prints new word to the screen.
*********************************/
void printNewWord(int newWord);



int main(){
    printf("%d",charToASI('c'));
}


#define BITS_PER_ASI_BLOCK 6

int charToASI(char ch) {
   /***      Apply all changes to the code below this line. DO NOT DELETE THIS COMMENT   ***/
        if (ch == '.')
        {
                return 1;
        }
        else if ('A' <= ch && ch <= 'Z')    //if an uppercase
        {
                return ch - 'A' + 2;
        }
        else if ('a' <= ch && ch <= 'z')    //if a lowercalse
        {
                return ch - 'a' + 28;
        }
        else if ('0' <= ch && ch <= '9')   //if a number
        {
                return ch - '0' + 54;
        }
        else
        {
        return -1;
        }
}
char ASItoChar(int ASIval) {
   int a,b;
   /*** replace 121 below with appropriate expression ***/
   if(ASIval < 1 || ASIval > 63) return '!';
   /*** replace 122 below with appropriate expression ***/
   a = (ASIval+24)/26;
   /*** replace 123 below with appropriate expression ***/
   b = (ASIval+24)%26;
   switch(a) {
      case(0):
         return '.';
      case(1):
         /*** replace a+124 below with appropriate expression ***/
         return 'A' + b;          //if an uppercase, set to fit the ascii of it
      case(2):
         /*** replace b+125 below with appropriate expression ***/
         return 'a' + b;          //if a lowercase, set to fit the ascii of it
      case(3):
         /*** replace 126 below with appropriate expression ***/
         return '0' + b;      //if a number, set to fit the ascii of it
      default:
      /*** do nothing here, This code should not be reached ***/
   } // end of switch
   /*** this code should not be reached ***/
   return '!';
}

int getASIblock(int newWord, int ind) {

   /*** replace 131 below with appropriate expression (bitwise and arithmetic operators) ***/
   return (newWord >> (BITS_PER_ASI_BLOCK * ind )) & ((1 << BITS_PER_ASI_BLOCK) - 1);
}

/*********************************
* Problem 1.4
* function isWordFull
* params:
* newWord (int)
* Return 1 if last ASI block of newWord is used
* and 0 otherwise
*********************************/
int isWordFull(int newWord) {
   /*** replace 141 below with appropriate expression ***/
    return (getASIblock(newWord, ((sizeof(newWord) * 8) / BITS_PER_ASI_BLOCK)-1) != 0);   //mutliplie by>
}


int newWordType(int newWord) {
   int ASIval=0, ind=0, type=ASIval;
   /*** loop until reaching ASI value 0 ***/
   for(ind=0; ind*BITS_PER_ASI_BLOCK<8*sizeof(int); ind++) {
   /***      Apply all changes to the code below this line. DO NOT DELETE THIS COMMENT   ***/
        ASIval = getASIblock(newWord, ind);
        if (ASIval == 0)
         {
                 break;
         }

        if ((ASIval >= 2 && ASIval <= 27) || (ASIval >= 28 && ASIval <= 53)) { //if is a letter
                 if (type == 2)
                {
                        type = 3; // Mixed (letters and digits)
                        break;    // We already know it's mixed, no need to continue
                }
                else {
                        type = 1; // Alphabetic
                 }
         }
        else if (ASIval >= 54 && ASIval <= 63)
        {
                if (type == 1)
                {
                         type = 3; // letters and digits
                         break;    // if mixed, the won't change
                }
                else {
                        type = 2; // Numeric
                }
        }
        else if (ASIval == 1) {
         // If type is already set to 1 or 2, it remains unchanged
        }
   /***      Apply all changes to the code above this line. DO NOT DELETE THIS COMMENT   ***/
   } // end of for
   return type;
}

int appendWordChar(int newWord, char ch) {
   /***      Apply all changes to the code below this line. DO NOT DELETE THIS COMMENT   ***/
        int maxBlock = (sizeof(newWord)*8)/BITS_PER_ASI_BLOCK;
        int ind = 0;
        int ASIval = charToASI(ch);
        if (ASIval == -1) //if the character to add isn't valid
        {
                return newWord;
        }
    //if the word is full and no more place

        for ( ind = 0; ind  < maxBlock ; ind++)
        {
                if (getASIblock(newWord, ind) == 0)
                {
            // Check if it's the partial block
            // Append the character to the new word
                newWord |= (ASIval << (BITS_PER_ASI_BLOCK * ind));
                return newWord;
                }
        }
         if (getASIblock(newWord,maxBlock) == 0 && ch == '.')
        {
                newWord |= (1 << (maxBlock *  BITS_PER_ASI_BLOCK));
        }

        return newWord;
   /***      Apply all changes to the code above this line. DO NOT DELETE THIS COMMENT   ***/
}

void printNewWord(int newWord) {

}

