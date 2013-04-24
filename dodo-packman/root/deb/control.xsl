<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output method="text" indent="yes"/>

  <xsl:include href="params.xsl"/>
  <xsl:include href="../common/functions.xsl"/>

  <xsl:template match="package">
   <xsl:text>Source: </xsl:text>
   <xsl:value-of select="$pkgname"/> 
   <xsl:text>
Section: devel</xsl:text>
   <xsl:text>
Priority: optional</xsl:text>
   <xsl:text>
Maintainer: Michael Fuchs &lt;michael.fuchs@unico-group.com&gt;</xsl:text>
   <xsl:text>
Standards-Version: 3.6.2</xsl:text>
   <xsl:text>

Package: </xsl:text>
    <xsl:value-of select="$pkgname"/> 
    <xsl:text>
Version: </xsl:text>
    <xsl:value-of select="/package/version"/> 
    <xsl:text>-</xsl:text>
    <xsl:value-of select="/package/release"/> 
    <xsl:text>
Architecture: all</xsl:text>
    <xsl:text>
Description: </xsl:text>
    <xsl:value-of select="./summary"/> 
    <xsl:text>
Depends: </xsl:text>
    <xsl:apply-templates select="dependencies"/>
    <xsl:text>
Provides: </xsl:text>
    <xsl:value-of select="./provides"/> 
    <xsl:text>
</xsl:text>
  </xsl:template>

  <xsl:template match="dependencies">
    <xsl:if test="@os='linux'">
      <xsl:for-each select="requires">
        <xsl:value-of select="@name"/>
        <xsl:text>, </xsl:text>
      </xsl:for-each>
    </xsl:if>
    <xsl:if test="@os='debian'">
      <xsl:for-each select="requires">
        <xsl:value-of select="@name"/>
        <xsl:text>, </xsl:text>
      </xsl:for-each>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
