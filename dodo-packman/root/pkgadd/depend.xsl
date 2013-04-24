<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="text" indent="yes"/>

  <xsl:include href="params.xsl"/>


  <xsl:template match="package">
    <xsl:apply-templates select="dependencies"/>
  </xsl:template>

  <xsl:template match="dependencies">

    <xsl:for-each select="requires">

      <xsl:text>P </xsl:text>
      <xsl:value-of select="concat($app,'-',@name)"/>
      <xsl:text> </xsl:text>
      <xsl:value-of select="."/>
      <xsl:text>
</xsl:text>

    </xsl:for-each>

  </xsl:template>

</xsl:stylesheet>
