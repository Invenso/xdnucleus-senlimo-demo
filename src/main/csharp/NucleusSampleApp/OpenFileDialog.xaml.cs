using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Shapes;

namespace NucleusSampleApp
{
    /// <summary>
    /// Interaction logic for OpenFileDialog.xaml
    /// </summary>
    public partial class OpenFileDialog : Window
    {
        public string FileUrl { get; set; }
        public OpenFileDialog()
        {
            InitializeComponent();
        }

        private void btnOpenFile_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                Process.Start(FileUrl);
            }
            catch (Exception ex)
            {
                MessageBox.Show("Could not find the item:\n" + ex.Message);
            }
        }

        private void btnOpenFolder_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                Process.Start("explorer.exe", "/select," + FileUrl);
            }
            catch (Exception ex)
            {
                MessageBox.Show("Could not find the item:\n" + ex.Message);
            }
        }
    }
}
