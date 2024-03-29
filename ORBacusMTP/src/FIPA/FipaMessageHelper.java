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
// IDL:FIPA/FipaMessage:1.0
//
final public class FipaMessageHelper
{
    public static void
    insert(org.omg.CORBA.Any any, FipaMessage val)
    {
        org.omg.CORBA.portable.OutputStream out = any.create_output_stream();
        write(out, val);
        any.read_value(out.create_input_stream(), type());
    }

    public static FipaMessage
    extract(org.omg.CORBA.Any any)
    {
        if(any.type().equivalent(type()))
            return read(any.create_input_stream());
        else
            throw new org.omg.CORBA.BAD_OPERATION();
    }

    private static org.omg.CORBA.TypeCode typeCode_;

    public static org.omg.CORBA.TypeCode
    type()
    {
        if(typeCode_ == null)
        {
            org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init();
            org.omg.CORBA.StructMember[] members = new org.omg.CORBA.StructMember[2];

            members[0] = new org.omg.CORBA.StructMember();
            members[0].name = "messageEnvelopes";
            members[0].type = EnvelopesHelper.type();

            members[1] = new org.omg.CORBA.StructMember();
            members[1].name = "messageBody";
            members[1].type = PayloadHelper.type();

            typeCode_ = orb.create_struct_tc(id(), "FipaMessage", members);
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:FIPA/FipaMessage:1.0";
    }

    public static FipaMessage
    read(org.omg.CORBA.portable.InputStream in)
    {
        FipaMessage _ob_v = new FipaMessage();
        _ob_v.messageEnvelopes = EnvelopesHelper.read(in);
        _ob_v.messageBody = PayloadHelper.read(in);
        return _ob_v;
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, FipaMessage val)
    {
        EnvelopesHelper.write(out, val.messageEnvelopes);
        PayloadHelper.write(out, val.messageBody);
    }
}
