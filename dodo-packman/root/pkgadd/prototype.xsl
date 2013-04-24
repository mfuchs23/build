<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="text" indent="yes"/>

  <xsl:include href="params.xsl"/>

  <xsl:template match="package">

    <xsl:text>i pkginfo
i copyright
</xsl:text>

    <xsl:if test="/package/requires">
      <xsl:text>i depend
</xsl:text>
    </xsl:if>

    <xsl:if test="/package/preinstall">
      <xsl:text>i preinstall
</xsl:text>
    </xsl:if>

    <xsl:if test="/package/postinstall">
      <xsl:text>i postinstall
</xsl:text>
    </xsl:if>

    <xsl:apply-templates select="files"/>
    
  </xsl:template>
  
  <xsl:template match="files">
    <xsl:apply-templates select="file"/>
  </xsl:template>
  
  
  <xsl:template match="file">
    
    <xsl:if test="not(@recursive) or @recursive != 'yes'">
      <xsl:apply-templates select="type"/>
      <xsl:text> none</xsl:text>
      <xsl:apply-templates select="path"/>
      <xsl:apply-templates select="mode"/>
      <xsl:apply-templates select="user"/>
      <xsl:apply-templates select="group"/>
      <xsl:text>
</xsl:text>
    </xsl:if>

  </xsl:template>

  <xsl:template match="type">

    <xsl:choose>

      <xsl:when test="text()='dir'">
        <xsl:text>d</xsl:text>
      </xsl:when>

      <xsl:when test="text()='file'">
        <xsl:text>f</xsl:text>
      </xsl:when>

    </xsl:choose>

  </xsl:template>

  <xsl:template match="path">
    <xsl:text> </xsl:text>
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="mode">
    <xsl:text> </xsl:text>
    <xsl:value-of select="."/>
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

  <xsl:template match="app">
    <xsl:value-of select="$app"/>
  </xsl:template>

</xsl:stylesheet>
