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

import com.mongodb.client.model.Filters;
import com.zerocracy.Farm;
import com.zerocracy.Par;
import com.zerocracy.pm.Footprint;
import com.zerocracy.pm.staff.Roles;
import com.zerocracy.tk.RqUser;
import com.zerocracy.tk.RsPage;
import com.zerocracy.tk.RsParFlash;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.logging.Level;
import org.bson.Document;
import org.cactoos.iterable.ItemAt;
import org.cactoos.iterable.Mapped;
import org.cactoos.scalar.IoCheckedScalar;
import org.takes.Response;
import org.takes.facets.fork.RqRegex;
import org.takes.facets.fork.TkRegex;
import org.takes.facets.forward.RsForward;
import org.takes.rs.RsWithStatus;
import org.takes.rs.xe.XeAppend;
import org.takes.rs.xe.XeChain;
import org.takes.rs.xe.XeTransform;

/**
 * Single claim take.
 *
 * @author Kirill (g4s8.public@gmail.com)
 * @version $Id$
 * @since 0.20
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class TkClaim implements TkRegex {
    /**
     * A farm.
     */
    private final Farm farm;

    /**
     * Ctor.
     * @param farm A farm
     */
    public TkClaim(final Farm farm) {
        this.farm = farm;
    }

    @Override
    public Response act(final RqRegex request) throws IOException {
        final RqProject pkt = new RqProject(this.farm, request);
        final String user = new RqUser(this.farm, request).value();
        final Roles roles = new Roles(pkt).bootstrap();
        final long cid = Long.valueOf(request.matcher().group(2));
        try (final Footprint ftp = new Footprint(this.farm, pkt)) {
            return new IoCheckedScalar<>(
                new ItemAt<>(
                    0,
                    src -> new RsWithStatus(HttpURLConnection.HTTP_NOT_FOUND),
                    new Mapped<Document, Response>(
                        doc -> new RsPage(
                            this.farm,
                            "/xsl/claim.xsl",
                            request,
                            () -> {
                                if (!doc.keySet().contains("public")
                                    && !roles.hasRole(user, "PO")) {
                                    throw new RsForward(
                                        new RsParFlash(
                                            new Par("Access denied").say(),
                                            Level.WARNING
                                        )
                                    );
                                }
                                return new XeChain(
                                    new XeAppend("project", pkt.pid()),
                                    new XeAppend(
                                        "claim",
                                        new XeTransform<>(
                                            doc.entrySet(),
                                            ent -> new XeAppend(
                                                ent.getKey(),
                                                ent.getValue().toString()
                                            )
                                        )
                                    )
                                );
                            }
                        ),
                        ftp.collection().find(
                            Filters.and(
                                Filters.eq("cid", cid),
                                Filters.eq("project", pkt.pid())
                            )
                        )
                    )
                )
            ).value();
        }
    }
}
