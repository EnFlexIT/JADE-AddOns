// **********************************************************************
//
// Generated by the ORBacus IDL to Java Translator
//
// Copyright (c) 2000
// Object Oriented Concepts, Inc.
// Billerica, MA, USA
//
// All Rights Reserved
//
// **********************************************************************

// Version: 4.0.5

package FIPA;

//
// IDL:FIPA/AgentID:1.0
//
final public class AgentIDHolder implements org.omg.CORBA.portable.Streamable
{
    public AgentID value;

    public
    AgentIDHolder()
    {
    }

    public
    AgentIDHolder(AgentID initial)
    {
        value = initial;
    }

    public void
    _read(org.omg.CORBA.portable.InputStream in)
    {
        value = AgentIDHelper.read(in);
    }

    public void
    _write(org.omg.CORBA.portable.OutputStream out)
    {
        AgentIDHelper.write(out, value);
    }

    public org.omg.CORBA.TypeCode
    _type()
    {
        return AgentIDHelper.type();
    }
}
