package org.wikidata.query.rdf.tool;

import static org.wikidata.query.rdf.tool.Matchers.binds;
import static org.wikidata.query.rdf.tool.StatementHelper.statement;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

/**
 * Validates the WikibaseDateExtension over the Blazegraph API.
 */
public class WikibaseDateExtensionIntegrationTest extends AbstractUpdateIntegrationTestBase {
    /**
     * Loads Q1 (universe) and validates that it can find it by searching for
     * things before some date very far in the past. Without our date extension
     * the start time doesn't properly parse in Blazegraph and doesn't allow
     * less than operations.
     */
    @Test
    public void bigBang() throws QueryEvaluationException {
        update(1, 1);
        StringBuilder query = new StringBuilder();
        query.append("PREFIX assert: <http://www.wikidata.org/entity/assert/>\n");
        query.append("SELECT * WHERE {\n");
        query.append("?s assert:P580 ?startTime .\n");
        query.append("FILTER (?startTime < \"-04540000000-01-01");
        if (randomBoolean()) {
            query.append("T00:00:00Z");
        }
        query.append("\"^^xsd:dateTime)\n");
        query.append("}");
        TupleQueryResult results = rdfRepository.query(query.toString());
        assertTrue(results.hasNext());
        BindingSet result = results.next();
        assertThat(result, binds("s", "Q1"));
        assertThat(result, binds("startTime", new LiteralImpl("-13798000000-01-01T00:00:00Z", XMLSchema.DATETIME)));
    }

    @Test
    public void date() throws QueryEvaluationException {
        List<Statement> statements = new ArrayList<>();
        statements.add(statement("Q23", "P569", new LiteralImpl("1732-02-22", XMLSchema.DATE)));
        rdfRepository.sync("Q23", statements);
        TupleQueryResult results = rdfRepository.query("SELECT * WHERE {?s ?p ?o}");
        BindingSet result = results.next();
        assertThat(result, binds("s", "Q23"));
        assertThat(result, binds("p", "P569"));
        assertThat(result, binds("o", new LiteralImpl("1732-02-22", XMLSchema.DATE)));
    }
}