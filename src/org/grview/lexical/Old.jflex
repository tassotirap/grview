/The line below can not be removed, otherwise the plugin do not works */
package org.eclipse.gef.asindesigner.model.analisador;
%%

%{
  private int comment_count = 0;
  /*The two lines below can not be removed or edited*/
  private TableNode TabT[];
  public void TabT(TableNode TbT[]){
	TabT = TbT;
  }
  /*If the simbol in text is in TabT this method returs its index otherwise returns
    -1. The parse uses this method to know if the recognized token is an reserved symbol or not.*/
  public int serchTabTSymbol(String text){
	int index = -1;
	int i=1;
	while(TabT[i]!=null){
		if((TabT[i].getName()).equals(text)){
			index = i;
			break;	
		}
		i++;
	}
	return index;
  }
 /*this method can not be removed, it is used by the error recovery routines. Basically this method call the 
 private method yypushbacck generated by the jflex. */
 public void pushback(int number){
	this.yypushback(number);
 }

%} 

%line
%char
%state COMMENT
%full


ALPHA=[A-Za-z]
DIGIT=[0-9]
NONNEWLINE_WHITE_SPACE_CHAR=[\ \t\b\012]
NEWLINE=\r|\n|\r\n
WHITE_SPACE_CHAR=[\n\r\ \t\b\012]
STRING_TEXT=(\\\"|[^\n\r\"]|\\{WHITE_SPACE_CHAR}+\\)*
COMMENT_TEXT=([^*/\n]|[^*\n]"/"[^*\n]|[^/\n]"*"[^/\n]|"*"[^/\n]|"/"[^*\n])*
Ident = {ALPHA}({ALPHA}|{DIGIT}|_)*

%% 

<YYINITIAL> {
  "," { return (new Yytoken(0,yytext(),yyline,yychar,yychar+1)); }
  ":" { return (new Yytoken(1,yytext(),yyline,yychar,yychar+1)); }
  ";" { return (new Yytoken(2,yytext(),yyline,yychar,yychar+1)); }
  "(" { return (new Yytoken(3,yytext(),yyline,yychar,yychar+1)); }
  ")" { return (new Yytoken(4,yytext(),yyline,yychar,yychar+1)); }
  "[" { return (new Yytoken(5,yytext(),yyline,yychar,yychar+1)); }
  "]" { return (new Yytoken(6,yytext(),yyline,yychar,yychar+1)); }
  "{" { return (new Yytoken(7,yytext(),yyline,yychar,yychar+1)); }
  "}" { return (new Yytoken(8,yytext(),yyline,yychar,yychar+1)); }
  "." { return (new Yytoken(9,yytext(),yyline,yychar,yychar+1)); }
  "+" { return (new Yytoken(10,yytext(),yyline,yychar,yychar+1)); }
  "*" { return (new Yytoken(12,yytext(),yyline,yychar,yychar+1)); }
  "/" { return (new Yytoken(13,yytext(),yyline,yychar,yychar+1)); }
  "=" { return (new Yytoken(14,yytext(),yyline,yychar,yychar+1)); }
  "<>" { return (new Yytoken(15,yytext(),yyline,yychar,yychar+2)); }
  "<"  { return (new Yytoken(16,yytext(),yyline,yychar,yychar+1)); }
  "<=" { return (new Yytoken(17,yytext(),yyline,yychar,yychar+2)); }
  ">"  { return (new Yytoken(18,yytext(),yyline,yychar,yychar+1)); }
  ">=" { return (new Yytoken(19,yytext(),yyline,yychar,yychar+2)); }
  "&"  { return (new Yytoken(20,yytext(),yyline,yychar,yychar+1)); }
  "|"  { return (new Yytoken(21,yytext(),yyline,yychar,yychar+1)); }
  ":=" { return (new Yytoken(22,yytext(),yyline,yychar,yychar+2)); }

  {NONNEWLINE_WHITE_SPACE_CHAR}+ { }

  "/*" { yybegin(COMMENT); comment_count++; }

  \"{STRING_TEXT}\" {
    String str =  yytext().substring(1,yylength()-1);
    return (new Yytoken(40,str,yyline,yychar,yychar+yylength()));
  }
  
  "-"*{DIGIT}+ { return (new Yytoken(42,yytext(),yyline,yychar,yychar+yylength())); }  

  {Ident} { return (new Yytoken(43,yytext(),yyline,yychar,yychar+yylength())); }  
}

<COMMENT> {
  "/*" { comment_count++; }
  "*/" { if (--comment_count == 0) yybegin(YYINITIAL); }
  {COMMENT_TEXT} { }
}

<<EOF>> { return(new Yytoken(99,"$",1,1,1));}


{NEWLINE} { }

. {
  System.out.println("Illegal character: <" + yytext() + ">");
}

