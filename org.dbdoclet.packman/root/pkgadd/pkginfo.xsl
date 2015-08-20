<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="text" indent="yes"/>

  <xsl:include href="params.xsl"/>
  <xsl:include href="../common/functions.xsl"/>

  <xsl:template match="package">

    <xsl:apply-templates select="name"/>
    <xsl:apply-templates select="architectures"/>
    <xsl:apply-templates select="version"/>
    <xsl:apply-templates select="summary"/>
    <xsl:text>CATEGORY=application
</xsl:text>
    <xsl:text>BASEDIR=</xsl:text>
    <xsl:value-of select="$basedir"/>
    <xsl:text>
</xsl:text>
    
  </xsl:template>

  <xsl:template match="name">
    
    <xsl:text>PKG=</xsl:text>
    <xsl:call-template name="pkgname"/>
    <xsl:text>
</xsl:text>
      
  </xsl:template>


  <xsl:template match="architectures">

    <xsl:text>ARCH=</xsl:text>
    <xsl:apply-templates select="architecture" />

  </xsl:template>

  <xsl:template match="architecture">

    <xsl:value-of select="."/>
    <xsl:if test="position() != last()">
      <xsl:text>,</xsl:text>
    </xsl:if>
    <xsl:text>
</xsl:text>

  </xsl:template>

  <xsl:template match="version">

    <xsl:text>VERSION=</xsl:text>
    <xsl:value-of select="."/>
    <xsl:text>
</xsl:text>

  </xsl:template>

  <xsl:template match="summary">

    <xsl:text>NAME=</xsl:text>
    <xsl:value-of select="."/>
    <xsl:text>
</xsl:text>

  </xsl:template>

</xsl:stylesheet>
