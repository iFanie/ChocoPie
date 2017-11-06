# ChocoPie
#### Write yummy code

Smart lifecycle, hassle free state restoration, automated view loading, unified Activity and Fragment behavior and much much more.
No worries about findViewById, no worries about bundles, no worries about backstacks and fragment management, just focus on your goal.

### Easy and intuitive lifecycle
Create - Initialize - Restore ... it cant get any simpler than that.
```
@Override
public void create() {
  //For listeners, tasks and anything that needs recreation
}

@Override
public void initialize() {
  //For elemnents that get an initial value and need to be restored
}

@Override
public void restore(@NonNull YumActivity savedInstance) {
  //For restoring values back to your fields
}
```

### Unique instance restoration
Reparable/Possessive instance restoration using simple class and/or field annotations.
```
@Restore
private Integer importantValue;

@Override
public void initialize() {
    importantValue = 9;
}

@Override
public void restore(@NonNull YumActivity savedInstance) {
    SampleActivity instance = (SampleActivity) savedInstance;
    importantValue = instance.importantValue;
}
```

Handle the restoration of fields using the `@InstanceRestoration` class annotation and the `@Restore`, `@Ignore` field annotations. Mix and match to your needs.
You can use the restoration mechanism **on _any_ field, even Views**. Be careful how you use them though, as restored Views will not have a Context (see kotlin example below, for a proper View restoration).

### Automated view loading
Provide a View ID naming convention or follow the default. Views will be automatically loaded for you.
Consider these layout elements.
```
<Button
    android:id="@+id/mainactivity_start"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Start" />

<Button
    android:id="@+id/mainactivity_stop"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Stop" />
```
And these class fields.
```
public class MainActivity extends YumActivity {

    private Button start;
    private Button stop;
    
    /.../
} 
```
Using the default View ID naming convention, `{{ className }}_{{ fieldName }} toLowerCase`, no extra code is needed to start using the above Buttons. You can modify the naming convention mixing between the class name, the field name and the field type.

### Unified Activity/Fragment behaviour
Extend YumActivity and YumFragment and make your codebase delicious.
```
public class SampleActivity extends YumActivity {

    @Override
    public int getContentView() { return R.layout.activity_sample; }

    @Override
    public void create() {}

    @Override
    public void initialize() {}

    @Override
    public void restore(@NonNull YumActivity savedInstance) {}
    
    /.../
}
```
See how simple Activities can be? It gets better, Fragments are just as simple.
```
public class SampleFragment extends YumFragment {

    @Override
    public int getContentView() { return R.layout.fragment_sample; }
    
    @Override
    public void create() {}

    @Override
    public void initialize() {}

    @Override
    public void restore(@NonNull YumFragment savedInstance) {}
    
    /.../
}
```

### Single Activity architecture
Easily implementable using your YumActivity as a container for your Fragment screens. Just provide a View for your Fragments.
```
public class SampleActivity extends YumActivity {

    @Nullable
    @Override
    public Integer getFragmentContainer() {
        return R.id.fragment_container;
    }

    @Override
    public void create() {}

    @Override
    public void initialize() {
        addFragment(new FragmentOne());
    }
    
    /.../
}
```

### Kotlin ready
Conflict free with the latest release.
```
class MainActivity : YumActivity() {
    override fun getContentView(): Int = R.layout.activity_main
    override fun getFragmentContainer(): Int? = R.id.fragment_root

    lateinit var start: Button
    lateinit var stop: Button
    lateinit var next: Button

    @Restore
    lateinit var label: TextView

    @Restore
    var counter: Int? = null

    override fun create() {
        next.setOnClickListener {
            addFragment(FragmentOne())
        }
    }

    override fun initialize() {
        label.text = "Hello world!"
        counter = 0
    }

    override fun restore(savedInstance: YumActivity) {
        val instance = savedInstance as MainActivity

        label.text = instance.label.text
        counter = instance.counter
    }
}
```
