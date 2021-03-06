package com.vose.fragment.action;

import android.app.ActionBar;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.voice.app.R;
import com.vose.activity.MainTabHostActivity;
import com.vose.activity.SingupLoginActivity;
import com.vose.data.model.util.ComponentSource;

/**
 * Created by jimmyhou on 1/16/15.
 */
public class RulesFragment extends Fragment{

    private static final String LOG_TAG = "RulesFragment";
    public static final String FRAGMENT_TAG = "FRAGMENT_RULES";

    private View fRootView;
    private Handler handler;
    private ComponentSource componentSource;

    public static  RulesFragment newInstance(ComponentSource componentSource) {

        RulesFragment rulesFragment = new  RulesFragment();
        rulesFragment.componentSource = componentSource;

        return rulesFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        fRootView = inflater.inflate(R.layout.fragment_rules, container, false);

        setCommunityGuidelinesView();
        setPrivacyPolicyView();
        setTermsOfServiceView();

        setActionBar();

        handler = new Handler();

        return fRootView;
    }


    private void setActionBar(){

        setHasOptionsMenu(true);

        getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        getActivity().getActionBar().setTitle(getString(R.string.rules_title));

    }


    private void setCommunityGuidelinesView(){

        TextView communityView = (TextView) fRootView.findViewById(R.id.community_guidelines);

        communityView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOnClickListener(getString(R.string.community_guidelines_title), getCommunityGuidelines());
            }
        });

    }


    private void setPrivacyPolicyView(){

        TextView privacyView = (TextView) fRootView.findViewById(R.id.privacy_policy);

        privacyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOnClickListener(getString(R.string.privacy_policy_title), getPrivacyPolicy());
            }
        });

    }

    private void setTermsOfServiceView(){

        TextView termsView = (TextView) fRootView.findViewById(R.id.terms_of_service);

        termsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setOnClickListener(getString(R.string.community_guidelines_title), getTermsOfServiceContents());

            }
        });

    }

    private void setOnClickListener(String fragmentTitle, String ruleContents){

        RuleContentsFragment ruleContentsFragment = RuleContentsFragment.newInstance(componentSource,fragmentTitle, ruleContents);

        if(componentSource == ComponentSource.MainTabHostActivity) {

            ((MainTabHostActivity)getActivity()).pushFragmentByNewThread(handler, ruleContentsFragment, android.R.id.tabhost, 0, RuleContentsFragment.FRAGMENT_TAG);

        }else if(componentSource == ComponentSource.SignupFragment){

            ((SingupLoginActivity)getActivity()).pushFragment(ruleContentsFragment, RuleContentsFragment.FRAGMENT_TAG);

        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){

       if(componentSource == ComponentSource.MainTabHostActivity) {

           ((MainTabHostActivity) getActivity()).hideActionBarMenuItems(menu);

           MenuItem overFlowMenu = menu.findItem(R.id.menu_overflow);
           overFlowMenu.setVisible(false);
       }

        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()){
            case android.R.id.home:

                getActivity().getSupportFragmentManager().popBackStack();

                if(componentSource == ComponentSource.MainTabHostActivity) {

                    //hide go back button
                    MainTabHostActivity mainActivity = (MainTabHostActivity) getActivity();

                    mainActivity.resetPreActivityActionBar();

                    mainActivity.getActionBar().setDisplayHomeAsUpEnabled(false);

                }

                return false;
        }

        return false;
    }


    private String getCommunityGuidelines(){

        return "Community Guidelines\n\n" +
                "We believe that everyone deserves a chance to be heard. We created Vose to be a safe place to say what’s on your mind and learn about the companies you work for and are interested in. These guidelines can help you get the most from what Vose has to offer.\n" +
                "Be open, be honest, and be yourself. Tell your stories, speak your mind, be creative, be responsible, and let’s make Vose a great community for sharing valuable information.\n" +
                "\n" +
                "Respect people’s privacy. People are on Vose to share anonymously.  Do not publicly share personal information about private people, like names, usernames, email addresses, addresses, phone numbers, and bank account information.\n" +
                "\n" +
                "Take what You Read With A Grain of Salt: While we wish to foster an open and constructive community, remember that part of sharing anonymously means that people are not as accountable for their comments.  The is the both the benefit and at times detriment of this sort of community.  Keep this in mind when reading comments, and remember that users don’t have to be honest.\n" +
                "\n" +
                "Make sure you own what you post. You need to have the rights to use the content you post on Vose. Respect copyright, trademark, rights of publicity and privacy and other legal rights.\n" +
                "\n" +
                "Be a real human and be nice to the internet. Vose is here for you to share with people in your corporate environment and industries, not for spam, compromising online accounts, illegal content, or content intended for commercial purposes like driving traffic to other sites. \n" +
                "\n" +
                "Report bad behavior. If you see something that violates our guidelines or may be unlawful, tell us. Reporting is the fastest and easiest way to let us know, and we review all reported content. You can always click on the share button on each post to report a post. If necessary, we’ll ban users who don’t follow our guidelines, and we reserve the right to escalate to law enforcement if we perceive a real risk of harm to people or property.\n" +
                "\n";
    }


    private String getPrivacyPolicy(){

        return "Your Privacy\n\n" +
                "Last Revised: January 15, 2014\n" +
                "Welcome to Vose -- The app that allows you to secretly share with others from VoiceApp Corp. (“Vose,” “we,” “us” or “our”).\n" +
                "This privacy policy explains how we collect, use and disclose information about you when you use our mobile application (the “App”), our Web site (the “Site”) and other online products and services that link to this privacy policy (collectively, the “Service”). By using the Service, you consent to our collection, use and disclosure of your personal information as described in this privacy policy. \n" +
                "Through the Service, you can secretly share messages, comments and other content (“Posts”) with certain other users of the Service. When you share a Post through the app, you are secretly sharing your Post with anyone else who has access to the Service. \n" +
                "Modifications\n" +
                "We may change this privacy policy from time to time. If we make changes, we will notify you by revising the date at the top of the policy and, in some cases, provide you with additional notice (such as adding a statement to our homepage or sending you an email notification). We encourage you to review the privacy policy periodically to stay informed about our practices and the ways you can help protect your privacy.\n" +
                "Collection of Information\n" +
                "Information You Provide to Us\n" +
                "We do not require any personally-identifying information in order to use the Service.  We will collect a username and password that you provide to us.  We will also collect user event information, such as taps on particular sections of the App, in order to improve the design and functionality of the App.  We may at our discretion provide aggregate, anonymous, non-identifiable click data to third parties for the purpose of research or marketing.  However, we will never reveal, and in fact cannot reveal whcih individual users this information corresponds to.\n" +
                "\n" +
                "We also will collect and retain any feedback provided to us by users and any information contained therein.  We will make no attempt to track or obtain the real identity of any users, except as required by the legal process of any relevant jurisdiction in which users are located and to which VoiceApp Corp is subject.\n" +
                "Information We Collect Automatically When You Use the Service\n" +
                "When you access or use our Service, we automatically collect information about you. This information is solely used by us for debugging and analytical purposes (e.g., to enhance the Service), and it includes:\n" +
                "Log Information: We log information about your use of the Service, including the type of browser you use, access times, IP address, Posts and pages viewed, Posts “loved,” and the page you visited before navigating to our Service.This data is not stored permanently, and only kept for no more than seven days for debugging purposes only.\n" +
                "Device Information: We collect information about the mobile device you use to access our Service, including the hardware model, operating system and version, unique device identifiers and mobile network information. This data is not stored, and only used to determine the version of your client in order to best visual experience.\n" +
                "Information Collected by Cookies and Other Tracking Technologies: We use various technologies to collect information, and this may include sending cookies to your computer or mobile device. Cookies are small data files stored on your hard drive or in device memory that help us to improve our Service and your experience, see which areas and features of our Service are popular and count visits. We may also collect information using web beacons (also known as “tracking pixels”). Web beacons are electronic images that may be used in our Service or emails and help deliver cookies, count visits, understand usage and campaign effectiveness and determine whether an email has been opened and acted upon. For more information about cookies, and how to disable them, please see “Your Choices” below.\n" +
                "Use of Information\n" +
                "We may use information about you for various purposes, including to:\n" +
                "Provide, maintain and improve our Service;\n" +
                "Provide and deliver the Service you request, process transactions and send you related information, including confirmations;\n" +
                "Send you technical notices, updates, confirmations, security alerts and support and administrative messages;\n" +
                "Respond to your comments, questions and requests and provide customer service;\n" +
                "Communicate with you about products, services, offers, promotions, rewards and events offered by Vose and others, and provide news and information we think will be of interest to you;\n" +
                "Monitor and analyze trends, usage and activities in connection with our Service and improve and personalize the Service;\n" +
                "Personalize and improve the Service and provide advertisements, content or features that match user profiles or interests;\n" +
                "Link or combine with information we get from others to help understand your needs and provide you with better service; and\n" +
                "Connect you with other users in your Contacts.\n" +
                "Sharing of Information\n" +
                "We may also share aggregated collected from the App information as follows:\n" +
                "With vendors, consultants and other service providers who need access to such information to carry out work on our behalf; \n" +
                "In connection with, or during negotiations of, any merger, sale of VoiceApp assets, financing or acquisition of all or a portion of our business to another company;\n" +
                "In response to a request for information if we believe disclosure is in accordance with any applicable law, regulation or legal process, or as otherwise required by any applicable law, rule or regulation;\n" +
                "If the content of a Post can reasonably be considered illegal or unlawful (or it seems like the Post is being used to engage in, encourage or promote such activities), in which case we may report your identity to proper authorities in order to protect the rights, property and safety of Vose, our users and/or others; and\n" +
                "With your consent or at your direction, including if we notify you through our Service that the information you provide will be shared in a particular manner and you provide such information. \n" +
                "Note that if you choose to include information about you in your Posts, such information will be shared with other users of the Service who receive such Posts as described in this privacy policy. We suggest that you do not include any identifiable information about you in your Posts. Also, please note that if you or other users choose to share a Post through a third party, such as Facebook, Twitter or an email client, any further collection and sharing of that Post by such third party or its users will be governed by that third party’s privacy policy, and we cannot control what any third party does with any Posts or other information that is shared with third parties.\n" +
                "How We Respond to Subpoenas from Courts\n" +
                "While we make it difficult to do so, it is still technically possible for us to connect your Posts with personal data we may receive as part of our hosting platform and event tracking software. This means that if a court asks us to disclose your identity, we may be compelled to provide whatever limited information we do possess. If we receive a subpoena or court order requesting that we disclose your information, we will attempt contact you if reasonably feasible before we disclose any information to give you time to fight the subpoena in court. In the United States, you may be able to fight the subpoena on the basis that it violates your First Amendment right to speak anonymously.\n" +
                "Security\n" +
                "We take reasonable measures to protect information about you from loss, theft, misuse and unauthorized access, disclosure, alteration and destruction.\n" +
                "Analytics Services Provided by Others\n" +
                "We may allow third parties to provide analytics services to us. These entities may use cookies, web beacons and other technologies to collect information about your use of the Service, including your IP address, Posts, Posts viewed, time spent on Posts, and conversion information. This information may be used by us and others to, among other things, analyze and track data, determine the popularity of certain content and better understand your online activity.\n" +
                "Push Notifications and Alerts\n" +
                "With your consent, we may send push notifications or alerts to your mobile device. You can deactivate these messages at any time by changing the notification settings on your mobile device.\n" +
                "Accessing and Correcting Information about You.\n" +
                "You may email us at voice.app.ca@gmail.com if you wish to know what personal information we hold about you or to correct any such information that is not accurate, complete or up to date. \n" +
                "Contact Us\n" +
                "If you have any questions or concerns about this privacy policy or any privacy issues, please email our Privacy Manager at voice.app.ca@gmail.com. Our Privacy Manager is responsible for coordinating the investigation of any concerns or complaints made to Vose about privacy issues and resolving the matter with a complainant. If you email our Privacy Manager at the email specified above, we will respond to concerns and complaints within a reasonable time after receipt (usually no more than 30 days).\n";
    }

    private String getTermsOfServiceContents(){

        return
                "Our Terms\n\n" +
                "Last Revised: January 15, 2014\n" +
                "These Terms of Service (“Terms”) apply to your access and use of VoiceApp’s mobile application (the “App”), VoiceApp’s website (“Site”), and other online products and services (collectively, the “Service”).\n" +
                "Highlights\n" +
                "1) Don’t post, link or otherwise make available on or through Vose any of the following:\n" +
                "Content that is illegal or unlawful;\n" +
                "Content that may infringe or violate any rights of any party; and\n" +
                "Viruses, corrupted data or other harmful, disruptive or destructive files or code.\n" +
                "2) Don't\n" +
                "Use Vose in any manner that could interfere with, disrupt, negatively affect or inhibit other Vose users or that could damage, disable, overburden or impair the functioning of Vose;\n" +
                "Collect any personal information about other users, or intimidate, threaten, stalk or otherwise harass other Vose users;\n" +
                "Circumvent or attempt to circumvent any features designed to protect Vose, Vose users, or third parties.\n" +
                "3) We change these Terms of Service every so often. If we make changes, we will notify you by revising the date at the top of the policy and, in some cases, provide you with additional notice (like on our homepage or over email).\n" +
                "4) These terms are between you and Vose, and not with Apple, Inc. While you may be subject to certain terms and conditions with Apple, Inc. by using Apple’s App Store or other products, these terms are specifically between You and Vose. Your use of the Vose Service is governed solely by these terms.\n" +
                "5) If you access or use Vose, it means you agree to all the terms below.\n" +
                "Accepting these Terms\n" +
                "If you access or use the Service, it means you agree to be bound by all of the terms below. So, before you use the Service, please read all of the terms. If you don’t agree to all of the terms below, please do not use the Service. Also, if a term does not make sense to you, please let us know.\n" +
                "Changes to these Terms\n" +
                "We reserve the right to change this Terms of Service from time to time. For example, we may need to change these Terms if we come out with a new feature. If we make changes, we will notify you by revising the date at the top of the policy and, in some cases, provide you with additional notice (such as adding a statement to our homepage or sending you an email notification). We encourage you to review the Terms of Service periodically to stay informed about our practices. If you continue to use the Service after the revised Terms have been posted, then you have accepted the changes to these Terms.\n" +
                "Whenever we make changes to these Terms, they are effective when the revised Terms are posted. If you continue to use the Service after the revised Terms have been posted, then you have accepted the changes to these Terms.\n" +
                "Privacy Policy\n" +
                "For information about how we collect and use information about users of the Service, please refer to our Privacy Policy.\n" +
                "Creating accounts\n" +
                "When you create an account you also agree to maintain the security of your password and accept all risks of unauthorized access to your account. If you discover or suspect any Service security breaches, please let us know as soon as possible.\n" +
                "Right to use the Service\n" +
                "Vose grants you a limited, nonexclusive, non-transferable and revocable license to access and use the Service. However, the App may only be used on mobile devices that you own or control. The terms of this license will also govern any upgrades provided by Vose that replace and/or supplement the original App, unless such upgrade is accompanied by a separate license, in which case the terms of that license will govern.\n" +
                "However, unless we expressly state otherwise, your right to use the Service does not include (i) publicly performing or publicly displaying the Service, (ii) modifying or otherwise making any derivative uses of the Service or any portion thereof, (iii) using any data mining, robots or similar data gathering or extraction methods, (iv) downloading (other than page caching) of any portion of the Service or any information contained therein, (v) reverse engineering or access to the Service in order to build a competitive product or service, or (vi) using the Service other than for its intended purposes. Should you do any of this, we may terminate your use of the Service, and may have infringed the copyright and other rights of Vose, which may subject you to prosecution and damages.\n" +
                "All information, materials and content of the Service including, but not limited to, text, graphics, data, formatting, graphs, designs, HTML, look and feel, photographs, music, sounds, images, software, videos, designs, typefaces, source and object code, format, queries, algorithms and other content is, between you and Vose, owned by Vose or is used with permission.\n" +
                "Your content & conduct\n" +
                "You agree to follow our Community Guide. You may not not post, link and otherwise make available on or through the Service any of the following:\n" +
                "Content that is illegal or unlawful;\n" +
                "Content that may infringe or violate any patent, trademark, trade secret, copyright, right of privacy, right of publicity or other intellectual or other right of any party; and\n" +
                "Viruses, corrupted data or other harmful, disruptive or destructive files or code.\n" +
                "Also, you agree that you will not do any of the following in connection with the Service or other users:\n" +
                "Use the Service in any manner that could interfere with, disrupt, negatively affect or inhibit other users from fully enjoying the Service or that could damage, disable, overburden or impair the functioning of the Service;\n" +
                "Collect any personal information about other users, or intimidate, threaten, stalk or otherwise harass other users of the Service;\n" +
                "Circumvent or attempt to circumvent any filtering, security measures, rate limits or other features designed to protect the Service, users of the Service, or third parties.\n" +
                "When you post, link or otherwise make available content to the Service, you grant us a nonexclusive, royalty-free, perpetual, irrevocable and fully sublicensable right to use, reproduce, modify, adapt, publish, translate, create derivative works from, distribute, perform and display such content throughout the world in any manner or media, on or off the App.\n" +
                "Feedback\n" +
                "Any suggestions, comments or other feedback provided by you to us with respect to the Service will constitute our confidential information. We will be free to use, disclose, reproduce, license and otherwise distribute, and exploit this feedback as we see fit, entirely without obligation or restriction of any kind on account of intellectual property rights or otherwise.\n" +
                "Disclaimers, Limitation of Liability and Indemnification\n" +
                "THE SERVICE AND ANY OTHER SERVICE AND CONTENT INCLUDED ON OR OTHERWISE MADE AVAILABLE TO YOU THROUGH THE SERVICE ARE PROVIDED TO YOU ON AN AS IS OR AS AVAILABLE BASIS WITHOUT ANY REPRESENTATIONS OR WARRANTIES OF ANY KIND. WE DISCLAIM ANY AND ALL OTHER WARRANTIES AND REPRESENTATIONS (EXPRESS OR IMPLIED, ORAL OR WRITTEN) WITH RESPECT TO THE SERVICE AND CONTENT INCLUDED ON OR OTHERWISE MADE AVAILABLE TO YOU THROUGH THE SERVICE WHETHER ALLEGED TO ARISE BY OPERATION OF LAW, BY REASON OF CUSTOM OR USAGE IN THE TRADE, BY COURSE OF DEALING OR OTHERWISE.\n" +
                "IN NO EVENT WILL Vose BE LIABLE TO YOU OR ANY THIRD PARTY FOR ANY DIRECT, SPECIAL, INDIRECT, INCIDENTAL, EXEMPLARY OR CONSEQUENTIAL DAMAGES OF ANY KIND ARISING OUT OF OR IN CONNECTION WITH THE SERVICE OR ANY OTHER SERVICE AND CONTENT INCLUDED ON OR OTHERWISE MADE AVAILABLE TO YOU THROUGH THE SERVICE, REGARDLESS OF THE FORM OF ACTION, WHETHER IN CONTRACT, TORT, STRICT LIABILITY OR OTHERWISE, EVEN IF WE HAVE BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES OR ARE AWARE OF THE POSSIBILITY OF SUCH DAMAGES.\n" +
                "You agree to defend, indemnify and hold us harmless from and against any and all costs, damages, liabilities, and expenses (including attorneys’ fees) we incur we in relation to, arising from, or for the purpose of avoiding, any claim or demand from a third party that your use of the Service or the use of the Service by any person using your account violates any applicable law or regulation, or the rights of any third party, and/or your violation of these Terms.\n" +
                "Third Party Software\n" +
                "The software you download consists of a package of components, including certain third party software (“Third Party Software” and together with the App, the “Package”) provided under separate license terms (the “Third Party Terms”). Your use of the Third Party Software in conjunction with the App in a manner consistent with the terms of these Terms is permitted, however, you may have broader rights under the applicable Third Party Terms and nothing in these Terms is intended to impose further restrictions on your use of the Third Party Software.\n" +
                "Modifications to the Service\n" +
                "Vose reserves the right in its sole discretion to review, improve, modify or discontinue, temporarily or permanently, the Service and/or any features, information, materials or content on the Service with or without notice to you. You agree that Vose will not be liable to you or any third party for any modification or discontinuance of the Service or any portion thereof.\n" +
                "Consent to Electronic Communications\n" +
                "By using the Service you agree that we may communicate with you electronically regarding administrative, security and other issues relating to your use of the Service, to the extent possible given the nature of the Service. You agree that any notices, agreements, disclosures or other communications that we send to you electronically will satisfy any legal communication requirements, including that such communications be in writing. To withdraw your consent from receiving electronic notice, please notify us at voice.app.ca@gmail.com.\n" +
                "Suspension/Termination\n" +
                "Vose may suspend and/or terminate your rights with respect to the Service for any reason or for no reason at all and with or without notice at Vose’s sole discretion. Suspension and/or termination may include restricting access to and use of the App. If your rights with respect to the Service are suspended and/or terminated, you agree to make no further use of the Service during suspension or after termination. All of the terms of these Terms (excluding the license grant) will survive any termination or suspension.\n" +
                "If Vose believes a Post you have made violates these Terms, Vose may make that Post invisible to other users without notifying you. Vose may delete the Post altogether without any notice to you or any other users.\n" +
                "Governing Law; Arbitration\n" +
                "PLEASE READ THE FOLLOWING PARAGRAPHS CAREFULLY BECAUSE THEY REQUIRE YOU TO ARBITRATE DISPUTES WITH Vose AND LIMIT THE MANNER IN WHICH YOU CAN SEEK RELIEF FROM Vose.\n" +
                "In the event of any controversy or claim arising out of or relating in any way to these Terms or the Service, you and Vose agree to consult and negotiate with each other and, recognizing your mutual interests, attempt to reach a solution satisfactory to both parties. If we do not reach settlement within a period of 60 days, then either of us may, by notice to the other demand mediation under the mediation rules of the American Arbitration Association in San Francisco, California. We both give up our right to litigate our disputes and may not proceed to arbitration without first attempting mediation, except that you and Vose are NOT required to arbitrate any dispute in which either party seeks equitable and other relief from the alleged unlawful use of copyrights, trademarks, trade names, logos, trade secrets or patents. Whether the dispute is heard in arbitration or in court, you and Vose will not commence against the other a class action, class arbitration or other representative action or proceeding.\n" +
                "If settlement is not reached within 60 days after service of a written demand for mediation, any unresolved controversy or claim will be resolved by arbitration in accordance with the rules of the American Arbitration Association before a single arbitrator in San Francisco, California. The language of all proceedings and filings will be English. The arbitrator will render a written opinion including findings of fact and law and the award and/or determination of the arbitrator will be binding upon the parties, and their respective administrators and assigns, and will not be subject to appeal. Judgment may be entered upon the award of the arbitrator in any court of competent jurisdiction. The expenses of the arbitration will be shared equally by the parties unless the arbitration determines that the expenses will be otherwise assessed and the prevailing party may be awarded its attorneys' fees and expenses by the arbitrator. It is the intent of the parties that, barring extraordinary circumstances, arbitration proceedings will be concluded within 90 days from the date the arbitrator is appointed. The arbitrator may extend this time limit only if failure to do so would unduly prejudice the rights of the parties. Failure to adhere to this time limit will not constitute a basis for challenging the award. Consistent with the expedited nature of arbitration, pre-hearing information exchange will be limited to the reasonable production of relevant, non-privileged documents, carried out expeditiously.\n" +
                "DMCA Copyright Policy\n" +
                "a. Notice.\n" +
                "If you are a copyright owner or an agent thereof and believe that any content available on our Service infringes your copyrights, you may, pursuant to the Digital Millennium Copyright Act (\"DMCA\"), notify our Copyright Agent by providing the following information in writing (see 17 U.S.C § 512(c)(3)):\n" +
                "A physical or electronic signature of a person authorized to act on behalf of the owner of an exclusive right that is allegedly infringed;\n" +
                "Identification of the copyrighted work claimed to have been infringed, or, if multiple copyrighted works at a single online site are covered by a single notification, a representative list of such works at that site;\n" +
                "Identification of the material that is claimed to be infringing or to be the subject of infringing activity and that is to be removed or access to which is to be disabled and information reasonably sufficient to permit the service provider to locate the material;\n" +
                "Information reasonably sufficient to permit the service provider to contact You, such as an address, telephone number and, if available, an electronic mail;\n" +
                "A statement that you have a good faith belief that use of the material in the manner complained of is not authorized by the copyright owner, its agent or the law; and\n" +
                "A statement that the information in the notification is accurate, and under penalty of perjury, that you are authorized to act on behalf of the owner of an exclusive right that is allegedly infringed.\n" +
                "You acknowledge that a failure to comply with all of the above requirements will result in an invalid notification.\n" +
                "Vose Corp’s designated Copyright Agent to receive notifications of claimed infringement is:\n" +
                "Vose DMCA Agent\n" +
                "Email: voice.app.ca@gmail.com\n" +
                "b. Counter-Notice.\n" +
                "If you believe that your content that was removed (or to which access was disabled) is not infringing, or that you have the authorization from the copyright owner, the copyright owner’s agent or pursuant to other law, to submit the content to Vose, you may send a counter-notice containing the following information to the Copyright Agent:\n" +
                "Your physical or electronic signature;\n" +
                "Identification of the content that was removed or to which access has been disabled and the location at which the content appeared before it was removed or disabled;\n" +
                "A statement that you have a good faith belief that the content was removed or disabled as a result of mistake or a misidentification of the content; and\n" +
                "Your name, address, telephone number and e-mail address and a statement that you will accept service of process from the person who provided notification of the alleged infringement.\n" +
                "If a counter-notice is received by the Copyright Agent, Vose may send a copy of the counter-notice to the original complaining party informing that person that it may replace the removed content or cease disabling it in 10 business days. Unless the copyright owner files an action seeking a court order against the content provider, member, or user, the removed content may be replaced, or access to it restored, in 10 to 14 business days or more after receipt of the counter-notice, at VoiceApp Corp’s sole discretion.\n" +
                "NOTICE REGARDING APPLE\n" +
                "While you may be subject to certain terms and conditions with Apple, Inc. by using Apple’s App Store or other products, these terms are specifically between You and Vose. Your use of the Vose Service is governed solely by these terms.\n" +
                "Notwithstanding any terms to the contrary in these Terms, the following additional terms will apply to the download of the App for use on the iPhone, iPod Touch or iPad:\n" +
                "You and Vose acknowledge that the terms are solely between you and Vose, and not with Apple, Inc. (“Apple”), and that Vose, not Apple, is solely responsible for the Service, the content thereof, maintenance, support services and warranty therefor, and addressing any claims relating thereto (e.g., product liability, legal compliance or intellectual property infringement). You acknowledge and agree that the availability of the App is dependent on the third party from which you received the App, e.g., the Apple iPhone App Store (“App Store”). You agree to pay all fees charged by the App Store in connection with the App (if any). You further acknowledge that the usage rules for the App are subject to any additional restrictions set forth in the Usage Rules for the Apple App Store Terms of Service as of the date you download the App. In the event of any conflict between the terms and conditions of the Usage Rules for the Apple App Store Terms of Service and the terms and conditions of these Terms, the terms and conditions of the Usage Rules for the Apple App Store Terms of Service will govern if they are more restrictive.\n" +
                "Scope of License\n" +
                "The license granted to you is limited to a non-transferable license to use the App on any iPhone, iPod Touch or iPad that you own or control as permitted by the Usage Rules set forth in the Apple App Store Terms of Service.\n" +
                "Maintenance and Support\n" +
                "Vose is solely responsible for providing maintenance and support services with respect to the App. You acknowledge and agree that Apple has no obligation whatsoever to furnish any maintenance and support services with respect to the App.\n" +
                "Warranty\n" +
                "You acknowledge and agree that Apple is not responsible for any product warranties, whether express or implied by law, with respect to the App. In the event of any failure of the App to conform to any applicable warranty, you may notify Apple, and Apple will refund the purchase price, if any, paid to Apple for the App by you; and to the maximum extent permitted by applicable law, Apple will have no other warranty obligation whatsoever with respect to the App. You also acknowledge and agree that to the extent that there are any applicable warranties, or any other claims, losses, liabilities, damages, costs or expenses attributable to any failure to conform to any such applicable warranty, such will be the sole responsibility of Vose. However, you understand and agree that in accordance with these Terms, Vose has disclaimed all warranties of any kind with respect to the App, and therefore, there are no warranties applicable to the App, except those implied by law.\n" +
                "Product Claims\n" +
                "You and Vose acknowledge and agree that as between Apple and Vose, Vose, not Apple, is responsible for addressing any of your claims or any third party claims relating to the App or your possession and/or use of the App, including, but not limited to: (i) product liability claims; (ii) any claim that the App fails to conform to any applicable legal or regulatory requirement; and (iii) claims arising under consumer protection or similar legislation.\n" +
                "Intellectual Property Rights\n" +
                "You and Vose acknowledge and agree that, in the event of any third party claim that the App or your possession and use of the App infringes that third party’s intellectual property rights, Vose, and not Apple, will be solely responsible for the investigation, defense, settlement and discharge of any such intellectual property infringement claim to the extent required under these Terms.\n" +
                "\n" +
                "Anonymity\n" +
                "The backend software of the Service at the time of these Terms, is hosted on Parse, which is a third-party hosting service.  You agree and acknowledge that any information posted on the App to the Service also is subject to the terms and condition of the Parse service.\n" +
                "You agree and acknowledge that while Vose will make no attempt to retain any personally identifiable information about any users, it is possible that the Parse platform may potentially collect and store personally identifiable information, consistent with its Privacy policies posted here: https://parse.com/about/privacy\n" +
                "You agree and acknowledge that you will not hold Vose responsible for any information collected or shared by Parse pursuant to its terms, conditions, and policies.  Further you agree that Vose may and will cooperate with any legally required process involving the disclosure of information about a user or the Service.\n" +
                "Legal Compliance\n" +
                "You represent and warrant that: (i) you are not located in a country that is subject to a U.S. Government embargo, or that has been designated by the U.S. Government as a “terrorist supporting” country; and (ii) you are not listed on any U.S. Government list of prohibited or restricted parties.\n" +
                "Developer Name and Address\n" +
                "Any end-user questions, complaints or claims with respect to the App should be directed to:\n" +
                "VoiceApp Corp.\n" +
                "Email: voice.app.ca@gmail.com\n" +
                "Third Party Beneficiary\n" +
                "The parties acknowledge and agree that Apple, and Apple’s subsidiaries, are third party beneficiaries of these Terms, and that, upon your acceptance of the terms and conditions of these Terms, Apple will have the right (and will be deemed to have accepted the right) to enforce any of the terms and conditions of these Terms against you as a third party beneficiary thereof.\n" +
                "\n" +
                "\n" +
                "\n";

    }

}
