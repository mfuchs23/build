<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output method="text" indent="yes"/>

  <xsl:include href="params.xsl"/>
  <xsl:include href="../common/functions.xsl"/>

  <xsl:template match="package">
    <xsl:text>#!/usr/bin/make -f
# -*- makefile -*-

configure: configure-stamp
configure-stamp:
	dh_testdir
	touch configure-stamp


build: build-stamp

build-stamp: configure-stamp 
	dh_testdir
	./build
	touch build-stamp

clean:
	dh_testdir
	dh_testroot
	rm -f build-stamp configure-stamp
	./prepare
	dh_clean 

install: build
	dh_testdir
	dh_testroot
	dh_clean -k 
	dh_installdirs
	./install

binary-indep: build install

binary-arch: build install
	dh_testdir
	dh_testroot
	dh_installchangelogs 
	dh_installdocs
#	dh_installexamples
#	dh_install
	dh_installmenu
	dh_desktop
#	dh_installdebconf	
#	dh_installlogrotate
#	dh_installemacsen
#	dh_installpam
#	dh_installmime
#	dh_installinit
#	dh_installcron
#	dh_installinfo
	dh_installman
	dh_link
	dh_strip
	dh_compress
	dh_fixperms
#	dh_perl
#	dh_python
#	dh_makeshlibs
	dh_installdeb
	dh_shlibdeps
	dh_gencontrol
	dh_md5sums
	dh_builddeb

binary: binary-indep binary-arch
.PHONY: build clean binary-indep binary-arch binary install configure
    </xsl:text>
  </xsl:template>

</xsl:stylesheet>
