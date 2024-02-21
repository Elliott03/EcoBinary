//
//  ContentView.swift
//  EcoBinary-iOS
//
//  Created by Elliott Phillips on 2/13/24.
//

import SwiftUI

struct RequestBody: Encodable {
    let commands: [String]
}

struct ContentView: View {
    @State private var privateIp: String = ""
    @State private var port: String = ""
    @State private var commands: String = ""
    @State private var commandsOutput: String = " "
    @State private var variableHeight: CGFloat = 0
    var body: some View {
        VStack {
            HStack{
                VStack {
                    Text("Private IP:")
                    TextField("",text: $privateIp)
                        .background(Color("InputBackgroundColor"))
                }
                Text("EcoBinary")
                VStack{
                    Text("Port:")
                    TextField(" ",text: $port)
                        .background(Color("InputBackgroundColor"))
                }
            }
            ScrollView {
                Text("\(commandsOutput)")
                    .frame(maxWidth: .infinity)
                    .background(Color("InputBackgroundColor"))
                    .padding(.bottom, 0)
                    .lineLimit(nil)
            }
            .frame(height: variableHeight)

            TextEditor(text: $commands)
                .textInputAutocapitalization(.never)
                .scrollContentBackground(.hidden)
                .background(Color("InputBackgroundColor"))
                .multilineTextAlignment(/*@START_MENU_TOKEN@*/.leading/*@END_MENU_TOKEN@*/)
                .lineLimit(nil)
                .padding(.top, 0)
            Button("Execute Commands") {
                let apiUrl = URL(string: "http://\(privateIp):\(port)/execute")!
                
                let commandList: [String] = self.commands.components(separatedBy: "\n")
                let requestBody = RequestBody(commands: commandList)
                
                guard let jsonData = try? JSONEncoder().encode(requestBody) else {
                    return
                }
                var request = URLRequest(url: apiUrl)
                request.httpMethod = "POST"
                request.httpBody = jsonData
                request.setValue("application/json", forHTTPHeaderField: "Content-Type")

                URLSession.shared.dataTask(with: request) { data, response, error in
                guard let data = data, error == nil else {
                    print("Error: \(error?.localizedDescription ?? "Unknown error")")
                    return
                }
                if let httpResponse = response as? HTTPURLResponse {
                    print("Status code: \(httpResponse.statusCode)")
                    if let responseString = String(data: data, encoding: .utf8) {
                        DispatchQueue.main.async {
                            var dataOutput = "\(responseString)".replacingOccurrences(of: "\\n", with: "\n")
                            dataOutput = String(dataOutput.dropFirst())
                            dataOutput = String(dataOutput.dropLast())
                            self.variableHeight = 200
                            self.commandsOutput = dataOutput
                        }
                    }
                }
                }.resume()
            }
            .buttonStyle(.borderedProminent)
        }
        .padding()
    }
}

#Preview {
    ContentView()
}
