package uk.co.tmdavies.nibanking;

import com.tterrag.registrate.AbstractRegistrate;

public class NIRegistrate extends AbstractRegistrate<NIRegistrate> {
    protected NIRegistrate(String modid) {
        super(modid);
    }

    public static NIRegistrate create(String modid) {
        return new NIRegistrate(modid);
    }
}
