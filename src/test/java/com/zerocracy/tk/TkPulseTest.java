/**
 * Copyright (c) 2016-2018 Zerocracy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to read
 * the Software only. Permissions is hereby NOT GRANTED to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.zerocracy.tk;

import com.jcabi.xml.XML;
import com.zerocracy.Farm;
import com.zerocracy.Project;
import com.zerocracy.farm.fake.FkFarm;
import com.zerocracy.farm.footprint.FtFarm;
import com.zerocracy.farm.props.PropsFarm;
import com.zerocracy.pm.ClaimOut;
import com.zerocracy.pm.Claims;
import com.zerocracy.pm.Footprint;
import javax.json.Json;
import javax.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.Take;
import org.takes.rq.RqFake;

/**
 * Test case for {@link TkPulse}.
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.22
 * @checkstyle JavadocMethodCheck (500 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class TkPulseTest {

    @Test
    public void rendersPulseJson() throws Exception {
        final Farm farm = new FtFarm(new PropsFarm(new FkFarm()));
        final Take take = new TkPulse(farm);
        final Project project = farm.find("@id='PULSETEST'").iterator().next();
        new ClaimOut().type("Hello").postTo(project);
        final XML xml = new Claims(project).iterate().iterator().next();
        try (final Footprint footprint = new Footprint(farm, project)) {
            footprint.open(xml);
            footprint.close(xml);
        }
        final JsonObject json = Json.createReader(
            take.act(new RqFake()).body()
        ).readObject();
        MatcherAssert.assertThat(
            json.getInt("total"),
            Matchers.greaterThanOrEqualTo(2)
        );
        MatcherAssert.assertThat(
            json.getInt("errors"),
            Matchers.greaterThanOrEqualTo(0)
        );
    }

}
