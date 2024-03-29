/******************************************************************
 * JADE - Java Agent DEvelopment Framework is a framework to develop
 * multi-agent systems in compliance with the FIPA specifications.
 * Copyright (C) 2002 TILAB S.p.A.
 *
 * This file is donated by Y'All B.V. to the JADE project.
 *
 *
 * GNU Lesser General Public License
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation,
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.
 * ***************************************************************/

package jade.addone.beangenerator;

/**
 *@author     C.J. van Aart - Y'All B.V.
 *@created    July 17, 2004
 *@version    1.0
 */


public class GenerateSlotHolder {

  public static void Generate(String packageName, String dir) {
    StringBuffer sb = new StringBuffer("package " + packageName + ";\n\n");
    sb.append("/* This is file is generated by the ontology bean generator.  \nDO NOT EDIT, UNLESS YOU ARE REALLY REALLY SURE WHAT YOU ARE DOING! */\n\n");
    sb.append("/** file: SlotHolder\n");
    sb.append(" * @author ontology bean generator (Y'All BV) \n");
    sb.append(" * @version " + OntologyBeanGeneratorUtil.getDate() + "\n");
    sb.append(" */\n");
    sb.append("\n");
    sb.append("public class SlotHolder {\n");
    sb.append("\n");
    sb.append("  public boolean equals(Object o) {\n");
    sb.append("    if (o instanceof SlotHolder) {\n");
    sb.append("      SlotHolder other = (SlotHolder) o;\n");
    sb.append("      if (other.className.equalsIgnoreCase(className) && other.slotName.equalsIgnoreCase(slotName)) {\n");
    sb.append("        return true;\n");
    sb.append("      }\n");
    sb.append("    }\n");
    sb.append("    return false;\n");
    sb.append("  }\n");
    sb.append("\n");
    sb.append("  public int hashCode() {\n");
    sb.append("    int retValue;\n");
    sb.append("\n");
    sb.append("    retValue = new String(className + \"_\" + slotName).toLowerCase().hashCode();\n");
    sb.append("    return retValue;\n");
    sb.append("  }\n");
    sb.append("\n");
    sb.append("  public SlotHolder(String className, String slotName) {\n");
    sb.append("    this.className = className;\n");
    sb.append("    this.slotName = slotName;\n");
    sb.append("  }\n");
    sb.append("  public String className;\n");
    sb.append("  public String slotName;\n");
    sb.append("}\n");
    OntologyBeanGeneratorUtil.writeFile(dir + "/SlotHolder.java", sb.toString());
  }

}