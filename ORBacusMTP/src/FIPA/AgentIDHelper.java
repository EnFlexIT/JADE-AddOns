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
final public class AgentIDHelper
{
    public static void
    insert(org.omg.CORBA.Any any, AgentID val)
    {
        org.omg.CORBA.portable.OutputStream out = any.create_output_stream();
        write(out, val);
        any.read_value(out.create_input_stream(), type());
    }

    public static AgentID
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
            org.omg.CORBA.StructMember[] members = new org.omg.CORBA.StructMember[4];

            members[0] = new org.omg.CORBA.StructMember();
            members[0].name = "name";
            members[0].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_string);

            members[1] = new org.omg.CORBA.StructMember();
            members[1].name = "addresses";
            members[1].type = orb.create_sequence_tc(0, URLHelper.type());

            members[2] = new org.omg.CORBA.StructMember();
            members[2].name = "resolvers";
            org.omg.CORBA.TypeCode content0;
            content0 = orb.create_recursive_tc(id());
            members[2].type = orb.create_sequence_tc(0, content0);

            members[3] = new org.omg.CORBA.StructMember();
            members[3].name = "userDefinedProperties";
            members[3].type = orb.create_sequence_tc(0, PropertyHelper.type());

            typeCode_ = orb.create_struct_tc(id(), "AgentID", members);
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:FIPA/AgentID:1.0";
    }

    public static AgentID
    read(org.omg.CORBA.portable.InputStream in)
    {
        AgentID _ob_v = new AgentID();
        _ob_v.name = in.read_string();
        int len0 = in.read_ulong();
        _ob_v.addresses = new String[len0];
        for(int i0 = 0 ; i0 < len0 ; i0++)
            _ob_v.addresses[i0] = URLHelper.read(in);
        int len1 = in.read_ulong();
        _ob_v.resolvers = new AgentID[len1];
        for(int i1 = 0 ; i1 < len1 ; i1++)
            _ob_v.resolvers[i1] = AgentIDHelper.read(in);
        int len2 = in.read_ulong();
        _ob_v.userDefinedProperties = new Property[len2];
        for(int i2 = 0 ; i2 < len2 ; i2++)
            _ob_v.userDefinedProperties[i2] = PropertyHelper.read(in);
        return _ob_v;
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, AgentID val)
    {
        out.write_string(val.name);
        int len0 = val.addresses.length;
        out.write_ulong(len0);
        for(int i0 = 0 ; i0 < len0 ; i0++)
            URLHelper.write(out, val.addresses[i0]);
        int len1 = val.resolvers.length;
        out.write_ulong(len1);
        for(int i1 = 0 ; i1 < len1 ; i1++)
            AgentIDHelper.write(out, val.resolvers[i1]);
        int len2 = val.userDefinedProperties.length;
        out.write_ulong(len2);
        for(int i2 = 0 ; i2 < len2 ; i2++)
            PropertyHelper.write(out, val.userDefinedProperties[i2]);
    }
}