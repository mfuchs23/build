<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="text" indent="yes"/>

  <xsl:include href="params.xsl"/>

  <xsl:template match="package">
    <xsl:apply-templates select="preinstall"/>
  </xsl:template>

  <xsl:template match="preinstall">
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

</xsl:stylesheet>
