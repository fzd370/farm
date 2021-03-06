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
package com.zerocracy.pm.staff.votes;

import com.zerocracy.Par;
import com.zerocracy.Project;
import com.zerocracy.pm.staff.Votes;
import com.zerocracy.pmo.Agenda;
import com.zerocracy.pmo.Awards;
import java.io.IOException;
import org.cactoos.iterable.LengthOf;

/**
 * Says "yes" when there is no room for this developer.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.12
 */
public final class VsNoRoom implements Votes {

    /**
     * The PMO.
     */
    private final Project pmo;

    /**
     * Ctor.
     * @param pkt Current project
     */
    public VsNoRoom(final Project pkt) {
        this.pmo = pkt;
    }

    @Override
    public double take(final String login, final StringBuilder log)
        throws IOException {
        final int total = new LengthOf(
            new Agenda(this.pmo, login).bootstrap().jobs()
        ).intValue();
        final double rate;
        final int max = this.threshold(login);
        if (total >= max) {
            rate = 1.0d;
            log.append(
                new Par(
                    "%d job(s) already, max is %d"
                ).say(total, max)
            );
        } else {
            rate = 0.0d;
            log.append(
                new Par(
                    "%d job(s) out of %d"
                ).say(total, max)
            );
        }
        return rate;
    }

    /**
     * Max jobs for user, depends on awards.
     * @param login A user
     * @return Jobs threshold
     * @throws IOException If fails
     * @checkstyle MagicNumberCheck (20 lines)
     */
    private int threshold(final String login) throws IOException {
        final int points = new Awards(this.pmo, login).bootstrap().total();
        final int max;
        if (points < 512) {
            max = 3;
        } else if (points < 2048) {
            max = 8;
        } else if (points < 4096) {
            max = 16;
        } else {
            max = 24;
        }
        return max;
    }
}
