                   Kerberos Version 5, Release 1.20

                            Release Notes
                        The MIT Kerberos Team

Copyright and Other Notices
---------------------------

Copyright (C) 1985-2022 by the Massachusetts Institute of Technology
and its contributors.  All rights reserved.

Please see the file named NOTICE for additional notices.

Documentation
-------------

Unified documentation for Kerberos V5 is available in both HTML and
PDF formats.  The table of contents of the HTML format documentation
is at doc/html/index.html, and the PDF format documentation is in the
doc/pdf directory.

Additionally, you may find copies of the HTML format documentation
online at

    https://web.mit.edu/kerberos/krb5-latest/doc/

for the most recent supported release, or at

    https://web.mit.edu/kerberos/krb5-devel/doc/

for the release under development.

More information about Kerberos may be found at

    https://web.mit.edu/kerberos/

and at the MIT Kerberos Consortium web site

    https://kerberos.org/

Building and Installing Kerberos 5
----------------------------------

Build documentation is in doc/html/build/index.html or
doc/pdf/build.pdf.

The installation guide is in doc/html/admin/install.html or
doc/pdf/install.pdf.

If you are attempting to build under Windows, please see the
src/windows/README file.

Reporting Bugs
--------------

Please report any problems/bugs/comments by sending email to
krb5-bugs@mit.edu.

You may view bug reports by visiting

https://krbdev.mit.edu/rt/

and using the "Guest Login" button.  Please note that the web
interface to our bug database is read-only for guests, and the primary
way to interact with our bug database is via email.

PAC transition
--------------

Beginning with release 1.20, the KDC will include minimal PACs in
tickets instead of AD-SIGNEDPATH authdata.  S4U requests (protocol
transition and constrained delegation) must now contain valid PACs in
the incoming tickets.  If only some KDCs in a realm have been upgraded
across version 1.20, the upgraded KDCs will reject S4U requests
containing tickets from non-upgraded KDCs and vice versa.

Triple-DES transition
---------------------

Beginning with the krb5-1.19 release, a warning will be issued if
initial credentials are acquired using the des3-cbc-sha1 encryption
type.  In future releases, this encryption type will be disabled by
default and eventually removed.

Beginning with the krb5-1.18 release, single-DES encryption types have
been removed.

Major changes in 1.20
---------------------

Administrator experience:

* Added a "disable_pac" realm relation to suppress adding PAC authdata
  to tickets, for realms which do not need to support S4U requests.

* Most credential cache types will use atomic replacement when a cache
  is reinitialized using kinit or refreshed from the client keytab.

* kprop can now propagate databases with a dump size larger than 4GB,
  if both the client and server are upgraded.

* kprop can now work over NATs that change the destination IP address,
  if the client is upgraded.

Developer experience:

* Updated the KDB interface.  The sign_authdata() method is replaced
  with the issue_pac() method, allowing KDB modules to add logon info
  and other buffers to the PAC issued by the KDC.

* Host-based initiator names are better supported in the GSS krb5
  mechanism.

Protocol evolution:

* Replaced AD-SIGNEDPATH authdata with minimal PACs.

* To avoid spurious replay errors, password change requests will not
  be attempted over UDP until the attempt over TCP fails.

* PKINIT will sign its CMS messages with SHA-256 instead of SHA-1.

Code quality:

* Updated all code using OpenSSL to be compatible with OpenSSL 3.

* Reorganized the libk5crypto build system to allow the OpenSSL
  back-end to pull in material from the builtin back-end depending on
  the OpenSSL version.

* Simplified the PRNG logic to always use the platform PRNG.

* Converted the remaining Tcl tests to Python.

krb5-1.20 changes by ticket ID
------------------------------

7707    Credential cache API does not support atomic reinitialization
8010    gss_store_cred should initialize ccache and work with collections
8970    Wrong Encryption types shown in MIT Kerberos Ticket Manager on Windows
8976    all-liblinks build target fails when symlinks not supported
8977    Allow kprop over more types of NATs
8978    Support host-based GSS initiator names
8980    Add APIs for marshalling credentials
8981    Documentation__krb5.conf
8983    Infer name type when creating principals
8988    Only require one valid pkinit anchor/pool value
8990    Add KCM_OP_GET_CRED_LIST for faster iteration
8991    Fix PKINIT memory leaks
8994    Fix gss-krb5 handling of high sequence numbers
8995    KCM interop issue with KRB5_TC_ flags
8997    Use KCM_OP_RETRIEVE in KCM client
8998    Simplify krb5_cccol_have_content()
8999    Add additional KRB5_TRACE points
9000    Fix multiple UPN handling in PKINIT client certs
9002    Check for undefined kadm5 policy mask bits
9003    Add duplicate check to kadm5_create_policy()
9009    Update IRC pointer in resources.rst
9010    Add MAXHOSTNAME guard in Windows public header
9011    Fix some principal realm canonicalization cases
9012    Allow kinit with keytab to defer canonicalization
9013    Fix kadmin -k with fallback or referral realm
9017    Clarify and correct interposer plugin docs
9019    make check fails: OSError: AF_UNIX path too long
9022    Potential integer overflows
9024    Find gss_get_mic_iov extensions in GSS modules
9025    Use version-independent OpenLDAP links in docs
9027    Add OpenLDAP advice to princ_dns.rst
9028    Constify name field in four plugin vtables
9031    Fix verification of RODC-issued PAC KDC signature
9032    Always use platform PRNG
9034    Use builtin MD4, RC4 for OpenSSL 3.0
9035    Avoid use after free during libkrad cleanup
9036    Support larger RADIUS attributes in libkrad
9037    Race condition in krb5_set_password()
9038    Issue an error from KDC on S4U2Self failures
9039    Fix PAC handling of authtimes after y2038
9040    Use 14 instead of 9 for unkeyed SHA-1 checksum
9041    Add PA-REDHAT-IDP-OAUTH2 padata type
9042    Don't fail krb5_cc_select() for no default realm
9043    Add PAC ticket signature APIs
9044    Replace AD-SIGNEDPATH with minimal PACs
9047    Avoid passing null for asprintf strings
9048    Pass client flag to KDB for client preauth match
9049    Add replace_reply_key kdcpreauth callback
9050    Implement replaced_reply_key input to issue_pac()
9051    Clarify certauth interface documentation


