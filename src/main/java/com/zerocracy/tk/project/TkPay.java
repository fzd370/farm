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
package com.zerocracy.tk.project;

import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Customer;
import com.stripe.net.RequestOptions;
import com.zerocracy.Farm;
import com.zerocracy.Par;
import com.zerocracy.Project;
import com.zerocracy.cash.Cash;
import com.zerocracy.farm.props.Props;
import com.zerocracy.pm.ClaimOut;
import com.zerocracy.pmo.Catalog;
import com.zerocracy.pmo.Pmo;
import com.zerocracy.tk.RqUser;
import com.zerocracy.tk.RsParFlash;
import java.io.IOException;
import java.util.logging.Level;
import org.cactoos.map.MapEntry;
import org.cactoos.map.SolidMap;
import org.takes.Response;
import org.takes.facets.fork.RqRegex;
import org.takes.facets.fork.TkRegex;
import org.takes.facets.forward.RsForward;
import org.takes.rq.RqGreedy;
import org.takes.rq.form.RqFormSmart;

/**
 * Pay page.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.19
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class TkPay implements TkRegex {

    /**
     * Farm.
     */
    private final Farm farm;

    /**
     * Ctor.
     * @param frm Farm
     */
    public TkPay(final Farm frm) {
        this.farm = frm;
    }

    @Override
    public Response act(final RqRegex req) throws IOException {
        final Project project = new RqProject(this.farm, req, "PO");
        final RqFormSmart form = new RqFormSmart(new RqGreedy(req));
        final String email = form.single("email");
        final String customer;
        try {
            customer = Customer.create(
                new SolidMap<String, Object>(
                    new MapEntry<>("email", email),
                    new MapEntry<>("source", form.single("token")),
                    new MapEntry<>(
                        "description",
                        String.format(
                            "%s/%s",
                            project.pid(),
                            new Catalog(this.farm).title(project.pid())
                        )
                    )
                ),
                new RequestOptions.RequestOptionsBuilder().setApiKey(
                    new Props(this.farm).get("//stripe/secret", "")
                ).build()
            ).getId();
        } catch (final APIException | APIConnectionException | CardException
            | AuthenticationException | InvalidRequestException ex) {
            throw new RsForward(
                new RsParFlash(ex),
                String.format("/p/%s", project.pid())
            );
        }
        final Cash amount = new Cash.S(
            String.format(
                "USD %.2f",
                // @checkstyle MagicNumber (1 line)
                Double.parseDouble(form.single("cents")) / 100.0d
            )
        );
        final String user = new RqUser(this.farm, req).value();
        new ClaimOut()
            .type("Funded by Stripe")
            .param("amount", amount)
            .param("stripe_customer", customer)
            .param("email", email)
            .author(user)
            .postTo(project);
        new ClaimOut().type("Notify user").token("user;yegor256").param(
            "message", new Par(
                "Project %s was funded for %s by @%s"
            ).say(project.pid(), amount, user)
        ).postTo(new Pmo(this.farm));
        return new RsForward(
            new RsParFlash(
                new Par(
                    "The project %s was successfully funded for %s.",
                    "The ledger will be updated in a few minutes."
                ).say(project.pid(), amount),
                Level.INFO
            ),
            String.format("/p/%s", project.pid())
        );
    }

}
