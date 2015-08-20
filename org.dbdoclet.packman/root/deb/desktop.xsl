<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output method="text" indent="yes"/>

  <xsl:include href="params.xsl"/>
  <xsl:include href="../common/functions.xsl"/>

  <xsl:template match="package">
    <xsl:text>[Desktop Entry]&#xa;</xsl:text>
    <xsl:text>Version=</xsl:text>
    <xsl:value-of select="/package/version"/>
    <xsl:text>&#xa;</xsl:text>
    <xsl:text>Encoding=UTF-8&#xa;</xsl:text>
    <xsl:text>Name=</xsl:text>
    <xsl:value-of select="/package/menu/title"/>
    <xsl:text>&#xa;</xsl:text>
    <xsl:text>GenericName=(</xsl:text>
    <xsl:value-of select="$pkgname"/> 
    <xsl:text>)&#xa;</xsl:text>
    <xsl:text>Comment=</xsl:text>
    <xsl:value-of select="/package/menu/comment"/> 
    <xsl:text>&#xa;</xsl:text>
    <xsl:text>Exec=</xsl:text>
    <xsl:value-of select="/package/menu/command"/> 
    <xsl:text>&#xa;</xsl:text>
    <xsl:text>Terminal=false&#xa;</xsl:text>
    <xsl:text>Type=Application&#xa;</xsl:text>
    <xsl:text>Icon=</xsl:text>
    <xsl:value-of select="/package/menu/icon"/> 
    <xsl:text>&#xa;</xsl:text>
    <xsl:text>Categories=</xsl:text>
    <xsl:value-of select="/package/menu/categories"/> 
    <xsl:text>&#xa;</xsl:text>
  </xsl:template>

</xsl:stylesheet>
