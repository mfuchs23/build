<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output method="text" indent="yes"/>

  <xsl:include href="params.xsl"/>
  <xsl:include href="../common/functions.xsl"/>

  <xsl:template match="package">
    <xsl:text>#!/bin/bash
# -*- shell-script -*-
PREFIX=/</xsl:text>
    <xsl:value-of select="$prefix"/>
    <xsl:text>
</xsl:text>
    <xsl:apply-templates select="./preinstall"/>
  </xsl:template>

</xsl:stylesheet>
