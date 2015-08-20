<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output method="text" indent="yes"/>

  <xsl:template match="package">

;Include Modern UI

!include "MUI.nsh"

; The name of the installer
Name "<xsl:value-of select="/package/name"/>"

; The file to write
OutFile "<xsl:value-of select="/package/name"/>.exe"

; The default installation directory
InstallDir "$PROGRAMFILES\<xsl:value-of select="/package/name"/>"

;--------------------------------
; Interface Settings

!define MUI_ABORTWARNING
!define MUI_ICON "root\Programme\<xsl:value-of select="/package/name"/>\icons\<xsl:value-of select="/package/name"/>.ico"
!define MUI_UNICON "root\Programme\<xsl:value-of select="/package/name"/>\icons\<xsl:value-of select="/package/name"/>.ico"

!insertmacro MUI_PAGE_LICENSE "root\Programme\<xsl:value-of select="/package/name"/>\License.txt"
; !insertmacro MUI_PAGE_COMPONENTS
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_INSTFILES
  
!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES

;--------------------------------
;Languages

!insertmacro MUI_LANGUAGE "english"
!insertmacro MUI_LANGUAGE "french"
!insertmacro MUI_LANGUAGE "german"
!insertmacro MUI_LANGUAGE "italian"
!insertmacro MUI_LANGUAGE "swedish"

Section install

  SetOutPath $INSTDIR
  File /r "root\Programme\<xsl:value-of select="/package/name"/>\*.*" 

  WriteUninstaller uninstall.exe

SectionEnd

Section Uninstall
  RMDir /r "$INSTDIR"
SectionEnd

;;
;; Callback functions
;;

Function .onInit
  !insertmacro MUI_LANGDLL_DISPLAY
FunctionEnd
  </xsl:template>

</xsl:stylesheet>
