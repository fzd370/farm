/**
 * Copyright (c) 2016 Zerocracy
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
package com.zerocracy.crews.gh;

import com.jcabi.github.Comment;
import com.jcabi.log.Logger;
import com.zerocracy.jstk.Stakeholder;
import java.io.IOException;

/**
 * He just says hello.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.1
 */
public final class StkHello implements Stakeholder {

    /**
     * Event.
     */
    private final Event event;

    /**
     * Ctor.
     * @param evt Event
     */
    public StkHello(final Event evt) {
        this.event = evt;
    }

    @Override
    public void work() throws IOException {
        final Comment.Smart comment = new Comment.Smart(this.event.comment());
        comment.issue().comments().post(
            String.format(
                "> %s%n%n@%s hey, how are you?",
                comment.body(),
                comment.author().login()
            )
        );
        Logger.info(
            this, "hello at %s#%d/%d",
            comment.issue().repo().coordinates(),
            comment.issue().number(),
            comment.number()
        );
    }
}