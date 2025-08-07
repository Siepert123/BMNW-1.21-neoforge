package nl.melonstudios.bmnw.computer.melonscript.op;

import nl.melonstudios.bmnw.computer.melonscript.MelonScriptVM;

public class OperationNoOp implements Operation {
    @Override
    public void run(MelonScriptVM vm) {

    }

    @Override
    public int getId() {
        return 0;
    }
}
