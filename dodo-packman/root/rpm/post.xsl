<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="text" indent="yes"/>

  <xsl:include href="params.xsl"/>
  <xsl:include href="../common/functions.xsl"/>

  <xsl:template match="package">#!/bin/bash

function usage {
  echo "$1"
  exit 255
}

DEST=""

while getopts "d:" arg
  do
  case $arg in
      d) DEST=$OPTARG;;
  esac
done

[ "${DEST}" = "" ] &amp;&amp; usage "You must specify the destination directory!"

install -d $DEST

echo "RPMS/*/<xsl:call-template name="pkgname"/>-<xsl:value-of select="/package/version"/>-<xsl:value-of select="/package/release"/>.*.rpm -> $DEST"
cp RPMS/*/<xsl:call-template name="pkgname"/>-<xsl:value-of select="/package/version"/>-<xsl:value-of select="/package/release"/>.*.rpm $DEST
[ $? -ne 0 ] &amp;&amp; exit 1

exit 0

  </xsl:template>

</xsl:stylesheet>
