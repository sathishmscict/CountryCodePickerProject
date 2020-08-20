package `in`.hbb20.countrycodepickerproject

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.hbb20.*
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import io.michaelrocks.libphonenumber.android.Phonenumber.PhoneNumber


/**
 * A simple [Fragment] subclass.
 */


class IntroductionFragment : Fragment() {


    private lateinit var buttonGo: Button
    private lateinit var llCcp: LinearLayout
    private lateinit var ccpDemo: CountryCodePicker
    private lateinit var edtMobileNo: EditText
    private lateinit var tvIntro: TextView
    private lateinit var countryCodePicker: CountryCodePicker
    private lateinit var etPhone: EditText
    lateinit var phoneUtil: PhoneNumberUtil
     var selectedCountry: CCPCountry? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_introduction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        assignViews()
        setClickListener()

        activity?.let {
            phoneUtil = PhoneNumberUtil.createInstance(it);
        }

        //dataBinding.ccp.registerCarrierNumberEditText(dataBinding.edtPhoneNumber)


    }

    private fun setClickListener() {
        buttonGo.setOnClickListener { (activity as ExampleActivity).viewPager.currentItem = 1 }


    }


    private fun assignViews() {

        tvIntro = view!!.findViewById(R.id.tvIntro)
        llCcp = view!!.findViewById(R.id.llCcp)
        edtMobileNo = view!!.findViewById<EditText>(R.id.edittext_mobileno)
        ccpDemo = view!!.findViewById(R.id.ccpDemo)
        //ccpDemo.registerCarrierNumberEditText(edtMobileNo)

        //ccpDemo.setInternationalFormattingOnly(true)
        //ccpDemo.setAutoDetectedCountry(true)
        ccpDemo.registerCarrierNumberEditText(edtMobileNo)
        ccpDemo.setCppClickListeners(object : CppClickListener {
            override fun openBottomSheet() {
                //cppDemo.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
                tvIntro.performClick()
            }
        });



       /* llCcp.setOnTouchListener(OnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                tvIntro.performClick()
                // Do what you want
                true
            } else false
        })*/


       /* llCcp.setOnClickListener({
            tvIntro.performClick()
        })*/

        /*edtMobileNo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(str: CharSequence?, start: Int, before: Int, count: Int) {
                try {

                    str.let {
                        val region = selectedCountry?.nameCode ?: "US"
                        val phoneNumber = phoneUtil.parse(it, region)
                        val mobileno: String = phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
                        Log.d("International No", mobileno)
                        edtMobileNo.removeTextChangedListener(this)
                        //edtMobileNo.clearFocus();
                        edtMobileNo.setText(mobileno)
                        edtMobileNo.addTextChangedListener(this)

                        //edtMobileNo.setText(mobileno)
                        //str?.insert(0,mobileno,0,str.length)


                    }

                } catch (e: NumberParseException) {
                    e.printStackTrace()
                }
            }

            override fun afterTextChanged(str: Editable?) {




            }

        })*/

        tvIntro.setOnClickListener {
            try {
                val bottomSheetFragment = BottomSheetFragment(object : CountrySelectListener {
                    override fun getSelectedCountry(ccpCountry: CCPCountry?) {
                        selectedCountry = ccpCountry
                        Log.d("DATA name", "${ccpCountry?.name}")
                        Log.d("DATA nameCode", "${ccpCountry?.nameCode}")
                        Log.d("DATA phoneCode", "${ccpCountry?.phoneCode}")

                        //val view = this.currentFocus
                       /* edtMobileNo?.let { v ->
                            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                            imm?.hideSoftInputFromWindow(v.windowToken, 0)
                        }*/

                        ccpDemo.onUserTappedCountry(selectedCountry)
                        updateHint()
                        Toast.makeText(activity, "data : ${ccpCountry?.name}", Toast.LENGTH_SHORT).show()
                    }
                })
                activity?.let {
                    bottomSheetFragment.show(it.supportFragmentManager, bottomSheetFragment.tag)

                }

            } catch (e: Exception) {
                Log.d("Error", " in open bottom sheet ${e.message}")
            }

            /*val swissNumberStr = "5555557003"
            val phoneUtil: PhoneNumberUtil = PhoneNumberUtil.createInstance(activity)
            try {
                val swissNumberProto: PhoneNumber = phoneUtil.parse(swissNumberStr, "US")
                Log.d("Introduction Fragment :","  $swissNumberProto")
            } catch (e: NumberParseException) {
                System.err.println("NumberParseException was thrown: " + e.toString())
            }*/

        }
        buttonGo = view!!.findViewById(R.id.button_letsGo)
        etPhone = view!!.findViewById(R.id.et_phone)
        countryCodePicker = view!!.findViewById(R.id.ccp)
        //countryCodePicker.setShowPhoneCode(false)
        //countryCodePicker.ccpDialogShowFlag = false;
        //countryCodePicker.ccpDialogShowFlag = false;
        //countryCodePicker.ccpDialogShowTitle = false;
        countryCodePicker.registerCarrierNumberEditText(etPhone)
    }

    private fun updateHint() {

            var formattedNumber: String? = ""
            val exampleNumber: PhoneNumber? = phoneUtil.getExampleNumberForType(selectedCountry?.nameCode, PhoneNumberUtil.PhoneNumberType.MOBILE)
            if (exampleNumber != null) {
                formattedNumber = exampleNumber.nationalNumber.toString() + ""
                //                Log.d(TAG, "updateHint: " + formattedNumber);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    formattedNumber = PhoneNumberUtils.formatNumber(getSelectedCountryCodeWithPlus() + formattedNumber, selectedCountry?.nameCode)
                } else {
                    formattedNumber = PhoneNumberUtils.formatNumber(getSelectedCountryCodeWithPlus() + formattedNumber)
                }
                if (formattedNumber != null) {
                    val countryCode = getSelectedCountryCodeWithPlus()
                    formattedNumber = formattedNumber.substring(countryCode!!.length).trim({ it <= ' ' })
                }
                //                Log.d(TAG, "updateHint: after format " + formattedNumber + " " + selectionMemoryTag);
            } else {
//                Log.w(TAG, "updateHint: No example number found for this country (" + getSelectedCountryNameCode() + ") or this type (" + hintExampleNumberType.name() + ").");
            }

            //fallback to original hint
            /*if (formattedNumber == null) {
                formattedNumber = originalHint
            }*/
            edtMobileNo.setHint(formattedNumber)

    }

    fun getSelectedCountryCodeWithPlus(): String? {
        //getSelectedCountryCode()
        return "+" + selectedCountry?.phoneCode
    }

}
