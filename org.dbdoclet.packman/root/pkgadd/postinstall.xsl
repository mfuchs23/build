<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="text" indent="yes"/>

  <xsl:include href="params.xsl"/>

  <xsl:template match="package">
    <xsl:text>
OS=""
TMP=`uname`
if [ "$TMP" = "SunOS" ] ; then
  OS="sol8"
elif [ "$TMP" = "Linux" ] ; then
  OS="linux"
else
  echo "FATAL ERROR: Unknown operating system $TMP"
  exit 255
fi
</xsl:text>
    <xsl:apply-templates select="postinstall"/>
    <xsl:text>exit 0</xsl:text>
  </xsl:template>

  <xsl:template match="postinstall">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="user">
    <xsl:text> </xsl:text>
    <xsl:value-of select="$user"/>
  </xsl:template>

  <xsl:template match="group">
    <xsl:text> </xsl:text>
    <xsl:value-of select="$group"/>
  </xsl:template>

  <xsl:template match="prefix">
    <xsl:value-of select="$prefix"/>
  </xsl:template>

  <xsl:template match="install-prefix">
    <xsl:text>/opt/snl</xsl:text>
  </xsl:template>

</xsl:stylesheet>
