<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="text" indent="yes"/>

  <xsl:include href="params.xsl"/>

  <xsl:template match="package">#!/bin/bash

function usage {
  echo "$1"
  exit 255
}

KITS=""

while getopts "k:" arg
  do
  case $arg in
      k) KITS=$OPTARG;;
  esac
done

[ "${KITS}" = "" ] &amp;&amp; usage "You must specify the kits directory!"

    <xsl:apply-templates select="archives"/>

exit 0

  </xsl:template>

  <xsl:template match="archives">
    <xsl:for-each select="archive[@arch=$os or @arch='noarch']">
cp <xsl:value-of select="concat('$KITS/',.)"/> SOURCES
[ $? -ne 0 ] &amp;&amp; exit 255
    </xsl:for-each>

exit 0
  </xsl:template>

</xsl:stylesheet>
