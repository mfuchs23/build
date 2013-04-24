<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output method="text"/>

  <xsl:template match="/">
    <xsl:apply-templates select="/package/name"/>
  </xsl:template>

  <xsl:template match="/package/name">
    <xsl:value-of select="."/>
  </xsl:template>

</xsl:stylesheet>