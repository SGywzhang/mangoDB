package client.cs4224c.parser;

import client.cs4224c.transaction.AbstractTransaction;

import java.util.Scanner;

public abstract class AbstractParser {

    public abstract AbstractTransaction parse(Scanner sc, String[] arguments);

}
