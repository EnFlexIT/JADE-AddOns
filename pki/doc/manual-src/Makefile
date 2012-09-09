goal=manual

TEX=latex
PDFVIEW=evince
TEX_TMPEXTS=.dvi .aux .toc .log

%.dvi: %.tex
	$(TEX) $<
	$(TEX) $<

%.pdf: %.dvi
	dvipdf $<


.PHONY: pdf view cleantmp clean

pdf: $(goal).pdf cleantmp

view: $(goal).pdf
	$(PDFVIEW) $< &>/dev/null &

cleantmp:
	-$(RM) $(addprefix $(goal), $(TEX_TMPEXTS))

clean: cleantmp
	-$(RM) $(addprefix $(goal), .pdf)


$(goal).pdf: $(goal).dvi
