<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output method="text" indent="yes"/>

  <xsl:include href="params.xsl"/>
  <xsl:include href="../common/functions.xsl"/>

  <xsl:template match="package">
    <xsl:text>#!/bin/bash
# -*- shell-script -*-
set -e

BUILD=`pwd -P`/BUILD
install -d $BUILD

    </xsl:text>
    <xsl:apply-templates select="./prepare"/>
    <xsl:apply-templates select="./archives" mode="extract"/>
  </xsl:template>

  <xsl:template match="archives" mode="extract">

    <xsl:variable name="counter" select="0"/>

    <xsl:for-each select="archive[@arch=$os or @arch='noarch']">
      <xsl:if test="@extract='yes'">
        <xsl:text>
rm -rf $BUILD/</xsl:text>
        <xsl:apply-templates select="archive-dir"/>
        <xsl:if test="substring(./archive-file, string-length(./archive-file) - string-length('.tar.gz') + 1) = '.tar.gz'">
          <xsl:text>
tar -xz -C $BUILD -f </xsl:text>
          <xsl:apply-templates select="archive-file"/>
        </xsl:if>
      </xsl:if>
    </xsl:for-each>

  </xsl:template>

</xsl:stylesheet>
