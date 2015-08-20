<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="text" indent="yes"/>

  <xsl:include href="params.xsl"/>

  <xsl:template match="package">


    <xsl:text>#!/bin/bash
set -x

function usage {
  echo "$1"
  exit 255
}

if [ "$PACKMAN_HOME" = "" ] ; then
  echo "Variable PACKMAN_HOME must be set!"
  exit 255
fi

KITS=""

while getopts "k:" arg
  do
  case $arg in
      k) KITS=$OPTARG;;
  esac
done

[ "${KITS}" = "" ] &amp;&amp; usage "You must specify the kits directory!"

</xsl:text>    

    <xsl:text>
PKGHOME=`pwd`

BUILD=$PKGHOME/build
SOURCES=$KITS

OS=""
TAR="tar"
TMP=`uname`
if [ "$TMP" = "SunOS" ] ; then
  OS="sol8"
  TAR="gtar"
elif [ "$TMP" = "Linux" ] ; then
  OS="linux"
else
  echo "FATAL ERROR: Unknown operating system $TMP"
  exit 255
fi

backup() {

  TIMESTAMP=`date +"%Y%m%d%H%M"`

  install -d backup
  mv $1 backup/$1-$TIMESTAMP      
}

install -d $BUILD
cd $BUILD

</xsl:text>

<xsl:apply-templates select="prepare"/>

<xsl:apply-templates select="archives"/>

<xsl:apply-templates select="build"/>

<xsl:apply-templates select="install"/>

<xsl:apply-templates select="files"/>

</xsl:template>

<xsl:template match="prepare">
  <xsl:text>
# -------
# PREPARE
# -------
cd $BUILD
</xsl:text>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="build">
  <xsl:text>
# -------
# BUILD
# -------
cd $BUILD
</xsl:text>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="install">
  <xsl:text>
# -------
# INSTALL
# -------
cd $BUILD
</xsl:text>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="archives">

  <xsl:text>cd $BUILD

</xsl:text>

  <xsl:for-each select="archive[@arch=$os or @arch='noarch']">
    <xsl:if test="@extract='yes'">

      <xsl:variable name="kit" select="concat('$KITS/',.)"/>

      <xsl:text>if [ ! -f </xsl:text>
      <xsl:value-of select="$kit"/>
      <xsl:text> ] ; then
  echo "FATAL ERROR: Kit </xsl:text>
      <xsl:value-of select="$kit"/>
      <xsl:text> doesn't exist!"
  exit 255
fi

gunzip -c </xsl:text>
      <xsl:value-of select="$kit"/>
      <xsl:text> | $TAR xvf -
</xsl:text>
    </xsl:if>
  </xsl:for-each>

</xsl:template>

<xsl:template match="files">

  <xsl:if test="count(file[@recursive='yes'])">

    <xsl:text>cd $PKGHOME
pkgproto</xsl:text>
    <xsl:for-each select="file[@recursive='yes']">
      <xsl:text> </xsl:text>
      <xsl:apply-templates select="path"/>
    </xsl:for-each>

    <xsl:text> | awk '{ printf("%s %s %s %s %s %s\n",$1,$2,$3,$4,"</xsl:text>
    <xsl:value-of select="$user"/>
    <xsl:text>","</xsl:text>    
    <xsl:value-of select="$group"/>
    <xsl:text>") }' | sed -e '/\$/d' &gt;&gt; prototype</xsl:text>
    
  </xsl:if>

exit 0

</xsl:template>

<xsl:template match="path">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="app">
  <xsl:value-of select="$app"/>
</xsl:template>

<xsl:template match="domain">
  <xsl:value-of select="$domain"/>
</xsl:template>

<xsl:template match="prefix">
  <xsl:value-of select="$prefix"/>
</xsl:template>

<xsl:template match="user">
  <xsl:value-of select="$user"/>
</xsl:template>

<xsl:template match="group">
  <xsl:value-of select="$group"/>
</xsl:template>

</xsl:stylesheet>
