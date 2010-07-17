package com.wesabe.api.util.rails.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.wesabe.api.util.rails.Inflector;

@RunWith(Enclosed.class)
public class InflectorTest {
	private static final String[] PAIRS = new String[] {
		"search",		"searches",
		"switch",		"switches",
		"fix",			"fixes",
		"box",			"boxes",
		"process",		"processes",
		"address",		"addresses",
		"case",			"cases",
		"stack",		"stacks",
		"wish",			"wishes",
		"fish",			"fish",
		"category",		"categories",
		"query",		"queries",
		"ability",		"abilities",
		"agency",		"agencies",
		"movie",		"movies",
		"archive",		"archives",
		"index",		"indices",
		"wife",			"wives",
		"safe",			"saves",
		"half",			"halves",
		"move",			"moves",
		"salesperson",	"salespeople",
		"person",		"people",
		"spokesman",	"spokesmen",
		"man",			"men",
		"woman",		"women",
		"basis",		"bases",
		"diagnosis",	"diagnoses",
		"diagnosis_a",	"diagnosis_as",
		"datum",		"data",
		"medium",		"media",
		"analysis",		"analyses",
		"node_child",	"node_children",
		"child",		"children",
		"experience",	"experiences",
		"day",			"days",
		"comment",		"comments",
		"foobar",		"foobars",
		"newsletter",	"newsletters",
		"old_news",		"old_news",
		"news",			"news",
		"series",		"series",
		"species",		"species",
		"quiz",			"quizzes",
		"perspective",	"perspectives",
		"ox",			"oxen",
		"photo",		"photos",
		"buffalo",		"buffaloes",
		"tomato",		"tomatoes",
		"dwarf",		"dwarves",
		"elf",			"elves",
		"information",	"information",
		"equipment",	"equipment",
		"bus",			"buses",
		"status",		"statuses",
		"status_code",	"status_codes",
		"mouse",		"mice",
		"louse",		"lice",
		"house",		"houses",
		"octopus",		"octopi",
		"virus",		"viruses",			// viri my ass
		"alias",		"aliases",
		"portfolio",	"portfolios",
		"vertex",		"vertices",
		"matrix",		"matrices",
		"matrix_fu",	"matrix_fus",
		"axis",			"axes",
		"testis",		"testes",
		"crisis",		"crises",
		"tax",			"taxes",
		"taxi",			"taxis",
		"rice",			"rice",
		"shoe",			"shoes",
		"horse",		"horses",
		"prize",		"prizes",
		"edge",			"edges",
		"cow",			"cows",			// kine? pfffft
		"sex",			"sexes",
		"move",			"moves",
		"love",			"loves"
	};
	
	public static class Pluralizing_The_Singular {
		private Inflector inflector;
		
		@Before
		public void setup() throws Exception {
			this.inflector = new Inflector();
		}
		
		@Test
		public void itPluralizesASingularWord() throws Exception {
			for (int i = 0; i < PAIRS.length; i += 2) {
				assertEquals(PAIRS[i+1], inflector.pluralize(PAIRS[i]));
			}
		}
		
		@Test
		public void itDoesNotPluralizeAnEmptyString() throws Exception {
			assertEquals("", inflector.pluralize(""));
		}
	}
	
	public static class Singularizing_The_Plural {
		private Inflector inflector;
		
		@Before
		public void setup() throws Exception {
			this.inflector = new Inflector();
		}
		
		@Test
		public void itSingularizesAPluralWord() throws Exception {
			for (int i = 0; i < PAIRS.length; i += 2) {
				assertEquals(PAIRS[i], inflector.singularize(PAIRS[i+1]));
			}
		}
		
		@Test
		public void itDoesNotSingularizeAnEmptyString() throws Exception {
			assertEquals("", inflector.singularize(""));
		}
	}
}
