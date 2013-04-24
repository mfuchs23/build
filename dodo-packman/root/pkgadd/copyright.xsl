<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="text" indent="yes"/>

  <xsl:include href="params.xsl"/>

  <xsl:template match="package">
    <xsl:apply-templates select="copyright"/>
  </xsl:template>

  <xsl:template match="copyright">
    <xsl:value-of select="."/>
  </xsl:template>

</xsl:stylesheet>
