package tasks;


import utils.Task;


public class idle extends Task {

    public boolean activate() {
        return false;
    }

    @Override
    public boolean execute() {
        return false;
    }
}