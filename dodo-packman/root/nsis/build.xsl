<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output method="text" indent="yes"/>

  <xsl:include href="params.xsl"/>
  <xsl:include href="../common/functions.xsl"/>

  <xsl:template match="install-dir">
    <xsl:value-of select="'Programme'"/>
  </xsl:template>

  <xsl:template match="package">
    <xsl:text>#!/bin/bash
# -*- shell-script -*-
set -e

BUILD=`pwd -P`/BUILD
install -d $BUILD

    </xsl:text>
    <xsl:apply-templates select="./build"/>
    <xsl:text>

exit 0
</xsl:text>
  </xsl:template>

</xsl:stylesheet>
