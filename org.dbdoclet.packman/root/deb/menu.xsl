<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output method="text" indent="yes"/>

  <xsl:include href="params.xsl"/>
  <xsl:include href="../common/functions.xsl"/>

  <xsl:template match="package">
    <xsl:text>?package(</xsl:text>
    <xsl:value-of select="$pkgname"/> 
    <xsl:text>):\&#xa;</xsl:text>
    <xsl:text>    needs="X11"\&#xa;</xsl:text>
    <xsl:text>    section="</xsl:text>
    <xsl:value-of select="/package/menu/section"/> 
    <xsl:text>"\&#xa;</xsl:text>
    <xsl:text>    title="</xsl:text>
    <xsl:value-of select="/package/menu/title"/> 
    <xsl:text>"\&#xa;</xsl:text>
    <xsl:text>    icon="</xsl:text>
    <xsl:value-of select="/package/menu/icon"/> 
    <xsl:text>"\&#xa;</xsl:text>
    <xsl:text>    command="</xsl:text>
    <xsl:value-of select="/package/menu/command"/> 
    <xsl:text>"</xsl:text>
  </xsl:template>

</xsl:stylesheet>
