/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.variance.vjax.xml;

import com.variance.mimiprotect.util.Utils;
import com.variance.vjax.android.VXMLNamespaceException;

/**
 * 
 * @author Marembo
 */
public final class VXMLVerifier {

	public static void verifyNamespace(VNamespace namespace) {
		// get the name part
		String name = namespace.getName();
		if (name.equalsIgnoreCase("xmlns")) {
			// get the prefix part
			String prf = namespace.getPrefix();
			if (!Utils.isNullOrEmpty(prf) && prf.startsWith("xml")) {
				throw new VXMLNamespaceException(
						"Invalid Namespace Declaration");
			}
		}
	}
}
