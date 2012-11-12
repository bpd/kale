# Makefile for the LaTeX Thesis
RM=del
#RM=rm -f

Thesis.pdf: Thesis.bbl
	pdflatex Thesis
  
  # fix cross-references
	pdflatex Thesis

Thesis.bbl:
  # create thesis.aux from thesis.tex
	pdflatex Thesis
  # create thesis.bbl from thesis.aux
	bibtex Thesis

clean:
	$(RM) *.dvi *.aux *.log *.lot *.lol *.lof *.toc *.out \
	*.bbl *.blg *.gz *.pdf *.ps
