/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rocks.prestodb.rest.slack;

import com.facebook.presto.Session;
import com.facebook.presto.testing.QueryRunner;
import com.facebook.presto.tests.AbstractTestQueryFramework;
import com.facebook.presto.tests.DistributedQueryRunner;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import static com.facebook.presto.testing.TestingSession.testSessionBuilder;

public class TestSlackQueries
        extends AbstractTestQueryFramework
{
    protected TestSlackQueries()
            throws Exception
    {
        super(createLocalQueryRunner());
    }

    public static QueryRunner createLocalQueryRunner()
            throws Exception
    {
        Session defaultSession = testSessionBuilder()
                .setCatalog("slack")
                .setSchema("default")
                .build();

        QueryRunner queryRunner = new DistributedQueryRunner(defaultSession, 1);
        queryRunner.installPlugin(new SlackPlugin());

        queryRunner.createCatalog(
                "slack",
                "slack",
                ImmutableMap.of("token", System.getenv("SLACK_TOKEN")));

        return queryRunner;
    }

    @Test
    public void showTables()
    {
        assertQuery("SHOW SCHEMAS FROM slack", "VALUES 'channel', 'im', 'information_schema'");
        computeActual("SHOW TABLES FROM slack.channel");
        computeActual("SHOW TABLES FROM slack.im");
    }

    @Test
    public void selectFromGeneral()
    {
        computeActual("SELECT * FROM slack.channel.general");
        computeActual("SELECT text FROM slack.channel.general");
    }

    @Test
    public void insertIntoChannel()
    {
        assertUpdate("INSERT INTO slack.channel.gko_tests VALUES (null, null, 'ala ma kota')", 1);
        assertUpdate("INSERT INTO slack.channel.gko_tests(text) VALUES ('ala ma kota 2')", 1);
    }

    @Test
    public void selectFromGKokosinski()
    {
        computeActual("SELECT * FROM slack.im.gkokosinski");
        computeActual("SELECT text FROM slack.im.gkokosinski");
    }

    @Test
    public void insertIntoGkokosinski()
    {
        assertUpdate("INSERT INTO slack.im.gkokosinski VALUES (null, null, 'ala ma kota')", 1);
        assertUpdate("INSERT INTO slack.im.gkokosinski(text) VALUES ('ala ma kota 2')", 1);
    }
}
