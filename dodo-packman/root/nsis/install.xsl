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

function fatal {
  echo $*
  exit -1
}

BUILD=`pwd -P`/BUILD
install -d $BUILD

    </xsl:text>
    <xsl:apply-templates select="./install"/>
    <xsl:text>
if [ "/</xsl:text>
    <xsl:value-of select="$prefix"/>
    <xsl:text>" != "</xsl:text>
    <xsl:value-of select="$buildroot"/>
    <xsl:value-of select="'Programme'"/>
    <xsl:text>" ] ; then</xsl:text>
    <xsl:apply-templates select="./files"/>
    <xsl:text>
  dummy=""
fi</xsl:text>
  </xsl:template>

  <xsl:template match="files">
    <xsl:text>
</xsl:text>
    <xsl:for-each select="file">
     <xsl:variable name="recursive">
        <xsl:choose>
          <xsl:when test="./@recursive='yes'">
            <xsl:text>true</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text>false</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>
      <xsl:variable name="target">
        <xsl:value-of select="$buildroot"/>
        <xsl:value-of select="'Programme'"/>
        <xsl:apply-templates select="path/text()"/>
      </xsl:variable>
      <xsl:text>  install -d </xsl:text>
      <xsl:call-template name="dirname">
        <xsl:with-param name="path" select="$target"/>
      </xsl:call-template>
      <xsl:text>
</xsl:text>
      <xsl:choose>
        <xsl:when test="./type='dir' and $recursive='false'">
          <xsl:text>  install -d </xsl:text>
          <xsl:value-of select="$target"/>
        </xsl:when>
        <xsl:when test="./type='dir' and $recursive='true'">
          <xsl:text>  [ -d </xsl:text>
          <xsl:apply-templates select="path"/>
          <xsl:text> ] &amp;&amp; cp -rf </xsl:text>
          <xsl:apply-templates select="path"/>
          <xsl:text> </xsl:text>
          <xsl:call-template name="dirname">
            <xsl:with-param name="path" select="$target"/>
          </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>  [ -f </xsl:text>
          <xsl:apply-templates select="path"/>
          <xsl:text> ] &amp;&amp; cp -f </xsl:text>
          <xsl:apply-templates select="path"/>
          <xsl:text> </xsl:text>
          <xsl:value-of select="$target"/>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:text>
</xsl:text> 
      <xsl:text>  [ ! -e </xsl:text>
      <xsl:value-of select="$target"/>
      <xsl:text> ] &amp;&amp; fatal "</xsl:text>
      <xsl:value-of select="$target"/>
      <xsl:text> does not exist!"
</xsl:text>
    </xsl:for-each>
  </xsl:template>
</xsl:stylesheet>
