package io.richardking.bound.binding

import android.support.v4.app.Fragment
import android.databinding.DataBindingComponent

/**
 * A Data Binding Component implementation for fragments.
 */
class FragmentDataBindingComponent(fragment: Fragment) : DataBindingComponent {
    private val adapter = FragmentBindingAdapters(fragment)

    override fun getFragmentBindingAdapters() = adapter
}