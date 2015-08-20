<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output method="text" indent="yes"/>

  <xsl:include href="params.xsl"/>
  <xsl:include href="../common/functions.xsl"/>

  <xsl:template match="package">
    <xsl:value-of select="$pkgname"/> 
    <xsl:text> (</xsl:text>
    <xsl:value-of select="./version"/> 
    <xsl:text>-</xsl:text>
    <xsl:value-of select="./release"/> 
    <xsl:text>) stable; urgency=low</xsl:text>
    <xsl:text>

  * Initial Release.

 -- Michael Fuchs &lt;michael.fuchs@unico-group.com&gt;  Tue,  1 Jan 2005 12:00:00 +0100

</xsl:text>
  </xsl:template>

</xsl:stylesheet>
