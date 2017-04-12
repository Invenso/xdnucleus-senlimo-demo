using System;
using System.Collections.Generic;
using System.IO;
using System.Threading;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Media;
using XperiDoNucleusRestServicesAPI.inputObjects;
using XperiDoNucleusRestServicesAPI.outputObjects;
using XperiDoNucleusRestServicesAPI.services;
using XperiDoNucleusRestServicesAPI.services.Requests;
using System.Windows.Threading;

namespace NucleusSampleApp
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        ServiceInvoker XdServices;
        bool finished;
        DocumentInfo info;
        Template template;
        string tbXMLText, tbNameText, filePath;
        FileExtension fileExtension;

        public MainWindow()
        {
            InitializeComponent();

            cbOutputFormat.ItemsSource = FileExtension.allValues();
            cbOutputFormat.SelectedItem = FileExtension.PDF;
            DocGen += On_DocGen;
        }

       

        /// <summary>
        /// Request the information from Nucleus and show it in your .NET application
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>

        #region Tab 1
        private void btnListTemplates_Click(object sender, RoutedEventArgs e)
        {
            GetTemplatesRequest request = new GetTemplatesRequest().GetThumbnails(true);
            List<Template> templates = XdServices.Invoke(request);
            listTemplates.ItemsSource = templates;
        }

        private void btnShowDynamicField_Click(object sender, RoutedEventArgs e)
        {
            Button pressedButton = (Button)sender;
            Template t = (Template)pressedButton.DataContext;

            GetTemplateDynamicFieldsRequest request = new GetTemplateDynamicFieldsRequest(t.Name);
            try
            {
                List<DynamicField> dfields = XdServices.Invoke(request);

                ListBox lb = new ListBox();
                lb.Height = 80;
                foreach (DynamicField field in dfields)
                {
                    lb.Items.Add(field.Name);
                }

                Grid element = (Grid)VisualTreeHelper.GetParent(pressedButton);
                element.Children.Clear();
                element.Children.Add(lb);
            }
            catch (NullReferenceException)
            {
                pressedButton.Content = "No dynamic field found";
                pressedButton.IsEnabled = false;
            }
        }
        #endregion

        #region Tab 2
        private void btnListLanguages_Click(object sender, RoutedEventArgs e)
        {
            GetLanguagesRequest request = new GetLanguagesRequest();
            List<Language> languages = XdServices.Invoke(request);
            listLanguages.ItemsSource = languages;
        }
        #endregion

        #region Tab 3
        private void btnListPrinters_Click(object sender, RoutedEventArgs e)
        {
            GetPrintersRequest request = new GetPrintersRequest();
            List<Printer> printers = XdServices.Invoke(request);
            listPrinters.ItemsSource = printers;
        }
        #endregion

        #region Tab 4

        // Only fill in the ComboBox when it gets opened.
        private void cbTemplates_DropDownOpened(object sender, EventArgs e)
        {
            GetTemplatesRequest request = new GetTemplatesRequest().GetThumbnails(true);
            List<Template> templates = XdServices.Invoke(request);
            cbTemplates.ItemsSource = templates;
        }



        public event EventHandler DocGen;
        /// <summary>
        /// Check if enough information is given, and fire a new thread that generates the doc
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void btnGenerate_Click(object sender, RoutedEventArgs e)
        {
            lblError.Content = string.Empty;
            if (cbTemplates.SelectedItem == null)
            {
                lblError.Content = "Please select a template.";
                return;
            }
            if (String.IsNullOrWhiteSpace(tbXML.Text))
            {
                lblError.Content = "Please fill in some XML";
                return;
            }
            if (String.IsNullOrWhiteSpace(tbName.Text))
            {
                lblError.Content = "Please give a name";
                return;
            }
            grdLoading.Visibility = Visibility.Visible;
            CreateDocumentData data = new CreateDocumentData("");
            template = (Template)cbTemplates.SelectedItem;
            data.InputData(template.DataSet.Name, tbXML.Text); //Get XML data from the textbox
            CreateDocumentWithXmlDataRequest request = new CreateDocumentWithXmlDataRequest(template.Name, data).DocumentFileName(tbName.Text)
                                                        .DocumentFileType((FileExtension) cbOutputFormat.SelectedItem) // Extension from the combobox
                                                        .UniqueIdentifierType(FileNameSuffix.TimestampDateTime);
            Thread th = new Thread(new ParameterizedThreadStart(ThreadCreateDoc));
            th.SetApartmentState(ApartmentState.STA);
            th.Start(request);
        }
        /*
        private void btnGenerateDb_Click(object sender, RoutedEventArgs e)
        {
            lblError.Content = string.Empty;
            if (cbTemplates.SelectedItem == null)
            {
                lblError.Content = "Please select a template.";
                return;
            }
            if (String.IsNullOrWhiteSpace(tbName.Text))
            {
                lblError.Content = "Please give a name";
                return;
            }
            grdLoading.Visibility = Visibility.Visible;
            var request = new CreateDocumentWithDbQueryRequest(((Template)cbTemplates.SelectedItem).Name);
            request.DbQueryName("Simple_query");
            request.DbQueryVariables(new List<string> { "1" });
            request.DocumentFileName(tbName.Text);
            request.DocumentFileType((FileExtension)cbOutputFormat.SelectedItem);
            request.UniqueIdentifierType(FileNameSuffix.Sequence);

            Thread th = new Thread(new ParameterizedThreadStart(ThreadCreateDoc));
            th.SetApartmentState(ApartmentState.STA);
            th.Start(request);
        }*/

        /// <summary>
        /// Generate the document.
        /// </summary>
        private void ThreadCreateDoc(object request)
        {
            Dispatcher.Invoke(() =>
            {
                template = (Template)cbTemplates.SelectedItem;
                tbXMLText = tbXML.Text;
                tbNameText = tbName.Text;
                fileExtension = (FileExtension)cbOutputFormat.SelectedItem;
            });


            
            info = XdServices.Invoke((CreateDocumentRequest)request);
            string requestId = info.RequestId;

            finished = info.Finished;
            while (!finished) // Loop that runs every 5 seconds to check if the document is created
            {
                Thread.Sleep(5000);
                var waitRequest = new DocumentFinishedRequest(requestId);

                info = XdServices.Invoke(waitRequest);
                finished = info.Finished;
            }
            var retrieveRequest = new RetrieveDocumentAsBase64Request(requestId);
            
            info = XdServices.Invoke(retrieveRequest);

            // Gets document folder, and creates the XperiDo Documents folder if it doesn't exist yet.
            string directory = System.IO.Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.MyDocuments), "XperiDo Documents");
            if (!Directory.Exists(directory))
                Directory.CreateDirectory(directory);
            filePath = System.IO.Path.Combine(directory, info.FileName);

            // Save the generated document
            Byte[] bytes = Convert.FromBase64String(info.Base64DocumentData);
            File.WriteAllBytes(filePath, bytes);

            if (DocGen != null)
                DocGen(this, EventArgs.Empty);
        }

        OpenFileDialog dialog;
        /// <summary>
        /// Event fired when a document got generated.
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void On_DocGen(object sender, EventArgs e)
        {
            Dispatcher.Invoke(() => { grdLoading.Visibility = Visibility.Hidden; });
            
            Dispatcher.Invoke(() => {
                GetPrintersRequest request = new GetPrintersRequest();
                List<Printer> printers = XdServices.Invoke(request);
                dialog = new OpenFileDialog();
                dialog.Owner = this;
                dialog.DocumentName.Content = info.FileName;
                dialog.FileUrl = filePath;
                dialog.cbPrinters.ItemsSource = printers;
                dialog.btnPrint.Click += BtnPrint_Click;
                dialog.btnEmail.Click += BtnEmail_Click;
                dialog.Show();
            });
        }

        private void tabControl_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
        }

        private void Button_Click(object sender, RoutedEventArgs e)
        {
            /// Connect to the rest service:
            XdServices = new ServiceInvoker(url.Text, username.Text, password.Text);
        }

        private void BtnEmail_Click(object sender, RoutedEventArgs e)
        {
            //MailDocumentRequest request = new MailDocumentRequest(info.RequestId, dialog.txtEmail.Text);
            //XdServices.Invoke(request);
        }

        private void BtnPrint_Click(object sender, RoutedEventArgs e)
        {
            Printer printer = (Printer)dialog.cbPrinters.SelectedItem;
            PrintingOptions printingOptions = new PrintingOptions(printer.Name);
            PrintDocumentRequest request = new PrintDocumentRequest(info.RequestId, printingOptions);
            XdServices.Invoke(request);
        }
        #endregion
    }
}
